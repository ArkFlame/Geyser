/*
 * Copyright (c) 2024 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.entity.type;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.cloudburstmc.math.matrix.Matrix4f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector4f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.debugshape.DebugShape;
import org.cloudburstmc.protocol.bedrock.data.debugshape.DebugText;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.DebugDrawerPacket;
import org.geysermc.geyser.entity.VanillaEntities;
import org.geysermc.geyser.entity.spawn.EntitySpawnContext;
import org.geysermc.geyser.entity.type.living.ArmorStandEntity;
import org.geysermc.geyser.translator.text.MessageTranslator;
import org.geysermc.geyser.util.ColorUtils;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.BooleanEntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.ByteEntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.IntEntityMetadata;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

// Note: 1.19.4 requires that the billboard is set to something in order to show, on Java Edition
@Getter
public class TextDisplayEntity extends DisplayBaseEntity {

    /**
     * The height offset per line of text in a text display entity when rendered
     * as an armor stand nametag on Bedrock Edition. This value was empirically adjusted
     * to match Java Edition's multi-line text centering behavior.
     */
    private static final float LINE_HEIGHT_OFFSET = 0.1414f;

    // Both editions use 10 font pixels per line including background padding, but Bedrock's font pixels are larger
    private static final float JAVA_LINE_HEIGHT = 0.25f; // 10px at 0.025 blocks per pixel
    private static final float BEDROCK_LINE_HEIGHT = 0.266071429f; // measured at scale 1
    private static final float BEDROCK_SCALE_FACTOR = JAVA_LINE_HEIGHT / BEDROCK_LINE_HEIGHT;

    /**
     * Java anchors the text display at the bottom of its background, whereas Bedrock anchors the first line of primitive text
     * 10% of its height below the top and scales from there.
     */
    private static final float BEDROCK_ANCHOR_OFFSET = JAVA_LINE_HEIGHT * 0.1f;

    private static final Color DEFAULT_BACKGROUND = new Color(0, 0, 0, 64);

    /**
     * The first Bedrock protocol version that has the debug drawer packet used for primitive text.
     */
    private static final int PRIMITIVE_TEXT_PROTOCOL = 818;

    /**
     * Escape hatch for servers that need to fall back to the metadata (armor stand nametag) rendering.
     */
    private static final boolean PRIMITIVE_TEXT_ENABLED = !Boolean.getBoolean("Geyser.DisablePrimitiveTextDisplays");

    /**
     * On Java Edition, armor stands can have a custom name shown additionally to
     * the text in the display. They are rendered separately, and can cross each other...
     */
    private @Nullable ArmorStandEntity secondEntity = null;
    private boolean isInvisible = false;
    private int lineCount;
    private final boolean primitiveText;
    private boolean sendUpdates = false;

    private @NonNull String unprocessedText = "";
    private @NonNull String text = "";
    private @Nullable Color color = null;
    private int lineWidth = 200;
    private @NonNull Color backgroundColor = DEFAULT_BACKGROUND;
    private byte textOpacity = -1;
    private boolean defaultBackground = false;
    private boolean shadow = false;
    private boolean seeThrough = false;
    private boolean hidden = false;

    public TextDisplayEntity(EntitySpawnContext context) {
        super(context);
        this.primitiveText = PRIMITIVE_TEXT_ENABLED && session.protocolVersion() >= PRIMITIVE_TEXT_PROTOCOL;
    }

    /**
     * @return whether this display is rendered with Bedrock primitive text instead of an armor stand nametag
     */
    public boolean usesPrimitiveText() {
        return primitiveText;
    }

    @Override
    protected void initializeMetadata() {
        super.initializeMetadata();
        // Remove armor stand body / hitbox
        this.metadata.put(EntityDataTypes.HITBOX, NbtMap.EMPTY);
        this.metadata.put(EntityDataTypes.SCALE, 0f);
        // Bedrock's NAMETAG_ALWAYS_SHOW renders the text through blocks. Java text displays
        // are occluded by default; setStyleFlags re-enables this when see_through is true.
        this.metadata.put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) 0);
    }

    @Override
    protected void setInvisible(boolean value) {
        // we'll keep the text display armor stand always invisible; would reveal the armor stand otherwise
        // but we would need to adjust the nametag
        isInvisible = value;
        this.updateNameTag();
    }

    @Override
    public void setCustomNameVisible(BooleanEntityMetadata entityMetadata) {
        super.setCustomNameVisible(entityMetadata);
        this.updateNameTag();
    }

    @Override
    public void setCustomName(EntityMetadata<Optional<Component>, ?> entityMetadata) {
        super.setCustomName(entityMetadata);
        this.updateNameTag();
    }

    @Override
    public void setNametagAlwaysShow(boolean value) {
        // no-op
    }

    @Override
    protected void setNameEntityData(String nametag) {
        // no-op
    }

    public void setTextMetadata(EntityMetadata<Component, ?> metadata) {
        Component component = metadata.getValue();

        if (!primitiveText) {
            // Metadata-based fallback (armor stand nametag) rendering
            this.metadata.put(EntityDataTypes.NAME, MessageTranslator.convertMessage(component, session.locale()));
            int oldLineCount = this.lineCount;
            this.lineCount = calculateLineCount(component);

            // If the line count changed, update the position to account for the new offset
            if (this.lineCount != oldLineCount) {
                setOffset(calculateLineOffset());
                moveAbsoluteRaw(position, yaw, pitch, headYaw, onGround, false);
            }
            return;
        }

        String message;
        Color color = null;

        Set<Color> colors = ColorUtils.extractAllColors(component);

        // Only tint the whole text when every part has the color, otherwise uncolored parts would get it too
        if (colors.isEmpty() || (colors.size() == 1 && isFullyColored(component, false))) {
            message = MessageTranslator.convertNoColorMessage(component, session.locale());
            color = colors.isEmpty() ? null : colors.iterator().next();
        } else {
            message = MessageTranslator.convertMessage(component, session.locale());
        }

        boolean changed = !message.equals(this.unprocessedText) || !Objects.equals(color, this.color);
        this.unprocessedText = message;
        this.color = color;

        if (changed) {
            processText();
        }
    }

    private static boolean isFullyColored(Component component, boolean inheritedColor) {
        boolean colored = inheritedColor || component.color() != null;
        if (!colored) {
            if (!(component instanceof TextComponent textComponent) || !textComponent.content().isEmpty()) {
                return false;
            }
        }
        for (Component child : component.children()) {
            if (!isFullyColored(child, colored)) {
                return false;
            }
        }
        return true;
    }

    public void setLineWidth(IntEntityMetadata metadata) {
        int value = metadata.getPrimitiveValue();
        if (value == this.lineWidth) {
            return;
        }
        this.lineWidth = value;
        if (primitiveText) {
            processText();
        }
    }

    public void setBackgroundColor(IntEntityMetadata metadata) {
        int value = metadata.getPrimitiveValue();

        int a = (value >> 24) & 0xFF;
        int r = (value >> 16) & 0xFF;
        int g = (value >> 8) & 0xFF;
        int b = value & 0xFF;

        Color backgroundColor = new Color(r, g, b, a);
        if (backgroundColor.equals(this.backgroundColor)) {
            return;
        }
        this.backgroundColor = backgroundColor;
        setDisplayDataDirty();
    }

    public void setTextOpacity(ByteEntityMetadata metadata) {
        byte value = metadata.getPrimitiveValue();
        if (value == this.textOpacity) {
            return;
        }
        this.textOpacity = value;
        setDisplayDataDirty();
    }

    public void setStyleFlags(ByteEntityMetadata metadata) {
        byte flags = metadata.getPrimitiveValue();

        boolean shadow = (flags & 0x01) == 1;
        boolean seeThrough = ((flags >> 1) & 0x01) == 1;
        boolean defaultBackground = ((flags >> 2) & 0x01) == 1;

        boolean changed = shadow != this.shadow || seeThrough != this.seeThrough || defaultBackground != this.defaultBackground;

        this.shadow = shadow;
        this.seeThrough = seeThrough;
        this.defaultBackground = defaultBackground;

        if (changed) {
            this.metadata.put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) (seeThrough ? 1 : 0));
            if (secondEntity != null) {
                secondEntity.getMetadata().put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) (seeThrough ? 1 : 0));
            }
            if (primitiveText) {
                setDisplayDataDirty();
            }
        }
    }

    private void processText() {
        String wrapped = MessageTranslator.wrapLines(this.unprocessedText, this.lineWidth);
        if (wrapped.equals(this.text)) {
            return;
        }
        this.text = wrapped;
        setDisplayDataDirty();
    }

    @Override
    public void spawnEntity() {
        super.spawnEntity();

        sendUpdates = true;

        if (!hidden) { // Only update if the display is shown
            updatePrimitiveText();
        }
    }

    @Override
    public void sendSpectateInvisible(boolean hidden) {
        super.sendSpectateInvisible(hidden);

        boolean previousHidden = this.hidden;

        this.hidden = hidden;

        if (this.hidden != previousHidden && sendUpdates) {
            if (hidden) { // Remove the display
                DebugDrawerPacket packet = new DebugDrawerPacket();
                packet.getShapes().add(new DebugShape(geyserId));
                session.sendUpstreamPacket(packet);
            } else { // Update the text for any changes and to show
                updatePrimitiveText();
            }
        }
    }

    @Override
    public void despawnEntity() {
        sendUpdates = false;
        if (secondEntity != null) {
            secondEntity.despawnEntity();
        }
        super.despawnEntity();

        if (primitiveText) {
            DebugDrawerPacket packet = new DebugDrawerPacket();
            packet.getShapes().add(new DebugShape(geyserId));
            session.sendUpstreamPacket(packet);
        }
    }

    @Override
    public void moveRelativeRaw(double relX, double relY, double relZ, float yaw, float pitch, float headYaw, boolean isOnGround) {
        float oldYaw = this.yaw;
        float oldPitch = this.pitch;
        super.moveRelativeRaw(relX, relY, relZ, yaw, pitch, headYaw, isOnGround);

        if (secondEntity != null) {
            secondEntity.moveRelativeRaw(relX, relY, relZ, yaw, pitch, headYaw, isOnGround);
        }

        if (getBillboard() != Billboard.CENTER && (yaw != oldYaw || pitch != oldPitch)) updatePrimitiveText();
    }

    @Override
    public void moveAbsoluteRaw(Vector3f position, float yaw, float pitch, float headYaw, boolean isOnGround, boolean teleported) {
        float oldYaw = this.yaw;
        float oldPitch = this.pitch;
        super.moveAbsoluteRaw(position, yaw, pitch, headYaw, isOnGround, teleported);

        if (secondEntity != null) {
            secondEntity.moveAbsoluteRaw(position.down(LINE_HEIGHT_OFFSET), yaw, pitch, headYaw, isOnGround, teleported);
        }

        if (getBillboard() != Billboard.CENTER && (yaw != oldYaw || pitch != oldPitch)) updatePrimitiveText();
    }

    @Override
    public void updateBedrockMetadata() {
        // Bundle metadata updates to ensure they aren't ignored
        if (secondEntity != null) {
            if (!secondEntity.valid) { // Spawn the entity once
                secondEntity.spawnEntity();
            } else {
                secondEntity.updateBedrockMetadata();
            }
        }
        super.updateBedrockMetadata();

        if (isDisplayDataDirty()) {
            // Metadata setters only store their values, so a whole metadata packet results in a single shape update
            updatePrimitiveText();
            clearDisplayDataDirty();
        }
    }

    @Override
    public Vector3f bedrockPosition() {
        // This *needs* to be handled differently for text displays, otherwise riding is messed up
        return primitiveText ? super.bedrockPosition().sub(getBaseTranslation()) : super.bedrockPosition();
    }

    @Override
    public void setVehicle(Entity vehicle) {
        super.setVehicle(vehicle);
        // This is important, any vehicle update *needs* to update the text, since the offset
        // applied may have changed due to a different hitbox
        updatePrimitiveText();
    }

    public void updatePrimitiveText() {
        if (!primitiveText || !sendUpdates || hidden) return;

        DebugText text = new DebugText();
        applyTransformation(text);
        text.setId(geyserId);
        // Text opacity still has to apply when the colors come from legacy codes, -1 is the fully opaque default
        Color baseColor = color == null && textOpacity != -1 ? Color.WHITE : color;
        text.setColor(
            baseColor == null ? null :
                new Color(
                    baseColor.getRed(),
                    baseColor.getGreen(),
                    baseColor.getBlue(),
                    textOpacity < 0 ? (textOpacity + 256) : textOpacity
                )
        );
        if (getVehicle() != null) {
            text.setAttachedToEntityId(getVehicle().geyserId);
            text.setPosition(text.getPosition().add(0, getVehicle().getBoundingBoxHeight() - 0.2f, 0));
        } else {
            text.setAttachedToEntityId(geyserId);
        }
        text.setUseRotation(!getBillboard().equals(Billboard.CENTER));
        // Java's view range is a multiplier of 64 blocks, Bedrock wants the distance itself
        text.setMaximumRenderDistance(getViewDistance() < 0 ? getViewDistance() : getViewDistance() * 64);
        text.setBackgroundColor(defaultBackground ? DEFAULT_BACKGROUND : backgroundColor);
        text.setDepthTest(!seeThrough);
        text.setDimension(session.getBedrockDimension().bedrockId());

        if (Boolean.getBoolean("Geyser.DebugTextDisplays")) {
            Vector3f position = bedrockPosition();
            Vector3f offset = text.getPosition();
            text.setText("ID: " + geyserId + ", Pos: " + position.getX() + ", " + position.getY() + ", " + position.getZ() + ", Off: " + offset.getX() + ", " + offset.getY() + ", " + offset.getZ());
        } else {
            text.setText(this.text);
        }

        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.getShapes().add(text);
        session.sendUpstreamPacket(packet);
    }

    private void applyTransformation(DebugText text) {
        // Java's transformation is T * L * S * R. Cloudburst's translate/rotate/scale pre-multiply, so build it with mul()
        // instead. The translation is left out here and added at the end.
        Matrix4f matrix = Matrix4f.createRotation(getLeftRotation())
            .mul(Matrix4f.createScaling(Vector4f.from(getBaseScale(), 1)))
            .mul(Matrix4f.createRotation(getRightRotation()));

        float m00, m01, m02, m10, m11, m12, m20, m21, m22;
        m00 = matrix.get(0, 0);
        m01 = matrix.get(0, 1);
        m02 = matrix.get(0, 2);
        m10 = matrix.get(1, 0);
        m11 = matrix.get(1, 1);
        m12 = matrix.get(1, 2);
        m20 = matrix.get(2, 0);
        m21 = matrix.get(2, 1);
        m22 = matrix.get(2, 2);

        float sx = (float) Math.sqrt(m00*m00 + m10*m10 + m20*m20);
        float sy = (float) Math.sqrt(m01*m01 + m11*m11 + m21*m21);
        float sz = (float) Math.sqrt(m02*m02 + m12*m12 + m22*m22);

        // A zero scale (often used to hide a display) would divide by zero and send NaN rotations to the client
        float ix = sx == 0 ? 0 : 1 / sx, iy = sy == 0 ? 0 : 1 / sy, iz = sz == 0 ? 0 : 1 / sz;
        float r00 = m00 * ix, r10 = m10 * ix, r20 = m20 * ix;
        float r01 = m01 * iy, r11 = m11 * iy, r21 = m21 * iy;
        float r02 = m02 * iz, r12 = m12 * iz, r22 = m22 * iz;

        float x = (float) Math.atan2(r21, r22);
        float y = (float) Math.atan2(-r20, Math.hypot(r00, r10));
        float z = (float) Math.atan2(r10, r00);

        float scale = ((sx + sy) / 2) * BEDROCK_SCALE_FACTOR; // sz doesn't affect the java scale with text displays, so don't use it

        // Move the bedrock anchor to where the top line sits within the java text, in the java text's local space
        int lines = countLines(this.text);
        Vector4f anchor = matrix.transform(0, JAVA_LINE_HEIGHT * lines - BEDROCK_ANCHOR_OFFSET, 0, 1);
        Vector3f translation = Vector3f.from(anchor.getX(), anchor.getY(), anchor.getZ());

        // Bedrock moves the text up by half of an unscaled line for every extra line, so undo that along the text's up axis
        if (lines > 1 && sy != 0) {
            Vector4f up = matrix.transform(0, 1, 0, 0).normalize().mul(BEDROCK_LINE_HEIGHT * (lines - 1) / 2);
            translation = translation.sub(up.getX(), up.getY(), up.getZ());
        }

        text.setPosition(translation.add(getBaseTranslation()));

        // I'm not even gonna pretend I know why x needs to be negative, but it does, and it works
        Vector3f rotation = Vector3f.from(-Math.toDegrees(x), Math.toDegrees(y), Math.toDegrees(z));

        Vector3f outputRotation = Vector3f.from(
            -getPitch(), (-getYaw()) + 180, 0
        );

        // Add rotation
        outputRotation = outputRotation.add(rotation);

        text.setRotation(outputRotation);

        text.setScale(scale);
    }

    private static int countLines(String text) {
        int lines = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }
        return lines;
    }

    public void updateNameTag() {
        // Text displays are special: customNameVisible must be set for the custom name to ever show
        if (this.nametag.isBlank() || isInvisible || !customNameVisible) {
            if (secondEntity != null) {
                secondEntity.despawnEntity();
                secondEntity = null;
            }
            return;
        }

        if (this.secondEntity == null) {
            secondEntity = new ArmorStandEntity(EntitySpawnContext.inherited(session, VanillaEntities.ARMOR_STAND, this, position.down(LINE_HEIGHT_OFFSET)));
        }
        secondEntity.getMetadata().put(EntityDataTypes.NAME, this.nametag);
        secondEntity.getMetadata().put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) (seeThrough ? 1 : 0));
        // Scale to 0 to show nametag
        secondEntity.setScale(0f);
        // No bounding box as we don't want to interact with this entity
        secondEntity.getMetadata().put(EntityDataTypes.WIDTH, 0.0f);
        secondEntity.getMetadata().put(EntityDataTypes.HEIGHT, 0.0f);
        secondEntity.getMetadata().put(EntityDataTypes.HITBOX, NbtMap.EMPTY);
    }

    private int calculateLineCount(@Nullable Component text) {
        if (text == null) {
            return 0;
        }
        String plain = PlainTextComponentSerializer.plainText().serialize(text);
        int length = plain.length();
        if (length == 0) {
            return 1;
        }
        // Equivalent to plain.split("\n").length without allocating the String[]: split drops
        // trailing empty lines, and an all-newline string yields zero lines.
        int newlines = 0;
        int lastNonNewline = -1;
        for (int i = 0; i < length; i++) {
            char c = plain.charAt(i);
            if (c == '\n') {
                newlines++;
            } else {
                lastNonNewline = i;
            }
        }
        if (lastNonNewline == -1) {
            return 0;
        }
        return 1 + newlines - (length - 1 - lastNonNewline);
    }

    /**
     * Calculates the Y offset needed to match Java Edition's text centering
     * behavior for multi-line text displays.
     * In Java Edition, multi-line text displays are centered vertically.
     * Only used by the metadata-based fallback rendering.
     *
     * @return the Y offset to apply based on the number of lines
     */
    public float calculateLineOffset() {
        if (lineCount == 0) {
            return 0;
        }
        return -0.6f + LINE_HEIGHT_OFFSET * lineCount;
    }
}

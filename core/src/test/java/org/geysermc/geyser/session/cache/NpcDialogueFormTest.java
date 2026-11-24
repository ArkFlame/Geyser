/*
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
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

package org.geysermc.geyser.session.cache;

import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.impl.simple.SimpleFormDefinition;
import org.geysermc.geyser.api.bedrock.form.NpcDialogueForm;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NpcDialogueFormTest {

    @Test
    void adaptsOntoCumulusSimpleFormResponses() throws Exception {
        AtomicInteger clickedButton = new AtomicInteger(-1);
        UUID entityUuid = UUID.randomUUID();
        NpcDialogueForm dialogue = NpcDialogueForm.builder()
                .title("Villager")
                .content("What do you want?")
                .button("Trade")
                .button("Leave")
                .entityUuid(entityUuid)
                .actionJson("[\"trade\",\"leave\"]")
                .npcData("{}")
                .validResultHandler(response -> clickedButton.set(response.clickedButtonId()))
                .build();

        assertEquals("Villager", dialogue.title());
        assertEquals(entityUuid, dialogue.entityUuid());
        assertEquals("{}", dialogue.npcData());

        SimpleForm form = dialogue.form();
        assertEquals(2, form.buttons().size());
        assertEquals("Leave", form.buttons().get(1).text());

        SimpleFormDefinition.instance().handleFormResponse(form, "1");
        assertEquals(1, clickedButton.get());
    }
}

package org.geysermc.geyser.translator.item;

import org.geysermc.geyser.item.Items;
import org.geysermc.geyser.item.type.Item;
import org.geysermc.geyser.registry.Registries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Items are created once per JVM. A test that touches them before the default component registries are loaded
 * leaves their resolvable components unset for every later test in the same JVM, which made the item tests depend on
 * test class order. Call {@link #repairItems()} after loading the component registries to make them order independent.
 */
final class ItemTestSupport {
    private ItemTestSupport() {}

    static void repairItems() {
        try {
            Field resolvable = Item.class.getDeclaredField("resolvableComponents");
            Field types = Item.class.getDeclaredField("resolvableComponentTypes");
            resolvable.setAccessible(true);
            types.setAccessible(true);
            for (Field field : Items.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || !Item.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                Item item = (Item) field.get(null);
                if (item == null || resolvable.get(item) != null) {
                    continue;
                }
                List<?> list = Registries.RESOLVABLE_DEFAULT_DATA_COMPONENTS.get(item.javaId());
                resolvable.set(item, list == null ? List.of() : list);
                types.set(item, List.of());
                if (list != null && !list.isEmpty()) {
                    List<Object> componentTypes = new java.util.ArrayList<>();
                    for (Object component : list) {
                        componentTypes.add(component.getClass().getMethod("type").invoke(component));
                    }
                    types.set(item, List.copyOf(componentTypes));
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}

package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.model.ClassNode;
import org.instancio.model.FieldNode;
import org.instancio.model.MapNode;
import org.instancio.model.Node;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

class InstancioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioDriver.class);

    private final Hierarchy hierarchy = new Hierarchy();
    private final GeneratorMap generatorMap = new GeneratorMap();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();

    private final InstancioContext context;
    private final GeneratorFacade generatorFacade;

    public InstancioDriver(InstancioContext context) {
        this.context = context;
        this.generatorFacade = new GeneratorFacade(context);
    }

    <C> C createClassEntryPoint(Class<C> rootClass) {

        final GeneratorResult<C> rootResult = generatorFacade.createInstanceOfClass(rootClass, null);
        final Object rootObject = rootResult.getValue();

        final Queue<CreateItem> queue = new ArrayDeque<>(rootResult.getFieldsToEnqueue());

        while (!queue.isEmpty()) {
            final CreateItem createItem = queue.poll();
            LOG.debug("-------------------\n\n-- {}\n", createItem);

            final Node node = createItem.getNode();

            if (node instanceof FieldNode) {
                final FieldNode fieldNode = (FieldNode) node; // FIXME
                final Field field = fieldNode.getField();

                if (context.isExcluded(field) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                final Object fieldOwner = createItem.getOwner();

                // TODO test.. what does it mean to return null GeneratorResult
                //
                final GeneratorResult<?> generatorResult = generatorFacade.create(createItem);
                if (generatorResult == null)
                    continue;

                final Object fieldValue = generatorResult.getValue();
                queue.addAll(generatorResult.getFieldsToEnqueue());
                ReflectionUtils.setField(fieldOwner, field, fieldValue);

            } else if (node instanceof ClassNode) {
                final ClassNode classNode = (ClassNode) node; // FIXME
                final FieldNode parent = (FieldNode) classNode.getParent();

                if (parent.getField().getType().isArray()) {
                    final Object owner = createItem.getOwner();

                    for (int i = 0; i < 2; i++) {
                        final GeneratorResult<?> result = generatorFacade.createInstanceOfClass(classNode.getKlass(), owner);

                        final Object createdValue = result.getValue();
                        Array.set(owner, i, createdValue);
                        //queue.addAll(result.getFieldsToEnqueue()); // XXX this child list is not complete!

                        queue.addAll(classNode.getChildren().stream()
                                .map(it -> new CreateItem(it, createdValue))
                                .collect(toList()));
                    }
                }

                if (Collection.class.isAssignableFrom(parent.getActualFieldType())) {
                    final Collection<Object> owner = (Collection<Object>) createItem.getOwner();

                    for (int i = 0; i < 2; i++) {
                        final GeneratorResult<?> result = generatorFacade.createInstanceOfClass(classNode.getKlass(), owner);

                        final Object createdValue = result.getValue();
                        owner.add(createdValue);
                        //queue.addAll(result.getFieldsToEnqueue()); // XXX this child list is not complete!

                        queue.addAll(classNode.getChildren().stream()
                                .map(it -> new CreateItem(it, createdValue))
                                .collect(toList()));
                    }
                }
            } else if (node instanceof MapNode) {
                final MapNode mapNode = (MapNode) node; // FIXME
                final ClassNode keyNode = mapNode.getKeyNode();
                final ClassNode valueNode = mapNode.getValueNode();

                final Map<Object, Object> owner = (Map<Object, Object>) createItem.getOwner();

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> keyResult = generatorFacade.createInstanceOfClass(keyNode.getKlass(), owner);
                    final Object mapEntryKey = keyResult.getValue();
                    queue.addAll(keyNode.getChildren().stream()
                            .map(it -> new CreateItem(it, mapEntryKey))
                            .collect(toList()));

                    final GeneratorResult<?> valueResult = generatorFacade.createInstanceOfClass(valueNode.getKlass(), owner);
                    final Object mapEntryValue = valueResult.getValue();
                    queue.addAll(valueNode.getChildren().stream()
                            .map(it -> new CreateItem(it, mapEntryValue))
                            .collect(toList()));

                    owner.put(mapEntryKey, mapEntryValue);
                }

            }
        }
        return (C) rootObject;
    }

    <C> List<C> createList(Class<C> klass) {
        return null;
    }
}

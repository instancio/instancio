javadoc_base_core = "https://javadoc.io/doc/org.instancio/instancio-core/latest/org.instancio.core/org/instancio"
javadoc_base_junit = "https://javadoc.io/doc/org.instancio/instancio-junit/latest/org.instancio.junit/org/instancio/junit"

def define_env(env):
    def add_class_javadoc(base_url, path, is_annotation=False):
        filename_with_ext = path.split('/')[-1]

        # Handle inner classes and extensions
        class_name = filename_with_ext.split('.')[0]
        if filename_with_ext.count('.') > 1:
            class_name = '.'.join(filename_with_ext.split('.')[:-1])

        display_name = f"@{class_name}" if is_annotation else class_name
        env.variables[class_name] = f'<a href="{base_url}/{path}" target="_blank">{display_name} &#8599;</a>'

    def add_method_javadoc(base_url, var_name, path_with_fragment, display_label):
        env.variables[var_name] = f'<a href="{base_url}/{path_with_fragment}" target="_blank">{display_label} &#8599;</a>'

    # instancio-core
    add_class_javadoc(javadoc_base_core, 'Assign.html')
    add_class_javadoc(javadoc_base_core, 'Assignment.html')
    add_class_javadoc(javadoc_base_core, 'Instancio.html')
    add_class_javadoc(javadoc_base_core, 'InstancioApi.html')
    add_class_javadoc(javadoc_base_core, 'Model.html')
    add_class_javadoc(javadoc_base_core, 'OnCompleteCallback.html')
    add_class_javadoc(javadoc_base_core, 'Random.html')
    add_class_javadoc(javadoc_base_core, 'Select.html')
    add_class_javadoc(javadoc_base_core, 'Selector.html')
    add_class_javadoc(javadoc_base_core, 'SelectorGroup.html')
    add_class_javadoc(javadoc_base_core, 'TargetSelector.html')
    add_class_javadoc(javadoc_base_core, 'When.html')
    add_class_javadoc(javadoc_base_core, 'feed/Feed.html')
    add_class_javadoc(javadoc_base_core, 'feed/FeedSpec.html')
    add_class_javadoc(javadoc_base_core, 'generator/AfterGenerate.html')
    add_class_javadoc(javadoc_base_core, 'generator/Generator.html')
    add_class_javadoc(javadoc_base_core, 'generator/GeneratorContext.html')
    add_class_javadoc(javadoc_base_core, 'generator/Hints.html')
    add_class_javadoc(javadoc_base_core, 'generator/ValueSpec.html')
    add_class_javadoc(javadoc_base_core, 'generator/hints/ArrayHint.html')
    add_class_javadoc(javadoc_base_core, 'generator/hints/CollectionHint.html')
    add_class_javadoc(javadoc_base_core, 'generator/hints/MapHint.html')
    add_class_javadoc(javadoc_base_core, 'generators/Generators.html')
    add_class_javadoc(javadoc_base_core, 'settings/FillType.html')
    add_class_javadoc(javadoc_base_core, 'settings/Keys.html')
    add_class_javadoc(javadoc_base_core, 'settings/Mode.html')
    add_class_javadoc(javadoc_base_core, 'settings/SettingKey.html')
    add_class_javadoc(javadoc_base_core, 'settings/Settings.html')
    add_class_javadoc(javadoc_base_core, 'spi/InstancioServiceProvider.GeneratorProvider.html')
    add_class_javadoc(javadoc_base_core, 'spi/InstancioServiceProvider.html')
    add_method_javadoc(javadoc_base_core, "withFillType", "InstancioObjectApi.html#withFillType(org.instancio.settings.FillType)", "withFillType(FillType)")
    add_method_javadoc(javadoc_base_core, "withMaxDepth", "InstancioApi.html#withMaxDepth(int)", "withMaxDepth(int)")
    add_method_javadoc(javadoc_base_core, "withNullable", "InstancioApi.html#withNullable(org.instancio.TargetSelector)", "withNullable(TargetSelector)")
    add_method_javadoc(javadoc_base_core, "withSeed", "InstancioApi.html#withSeed(long)", "withSeed(long)")
    add_method_javadoc(javadoc_base_core, "withSetting", "InstancioApi.html#withSetting(org.instancio.settings.SettingKey,V)", "withSetting(SettingKey&lt;T&gt;, T)")
    add_method_javadoc(javadoc_base_core, "withSettings", "InstancioApi.html#withSettings(org.instancio.settings.Settings)", "withSettings(Settings)")

    # instancio-junit
    add_class_javadoc(javadoc_base_junit, 'Given.html', is_annotation=True)
    add_class_javadoc(javadoc_base_junit, 'InstancioExtension.html')
    add_class_javadoc(javadoc_base_junit, 'InstancioSource.html', is_annotation=True)
    add_class_javadoc(javadoc_base_junit, 'Seed.html', is_annotation=True)
    add_class_javadoc(javadoc_base_junit, 'WithSettings.html', is_annotation=True)

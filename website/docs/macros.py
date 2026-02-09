javadoc_base_core = "https://javadoc.io/doc/org.instancio/instancio-core/latest/org.instancio.core/org/instancio"
javadoc_base_junit = "https://javadoc.io/doc/org.instancio/instancio-junit/latest/org.instancio.junit/org/instancio/junit"


def define_env(env):
    def create_link(url, label):
        return f'<a href="{url}" target="_blank">{label} &#8599;</a>'

    def add_class_javadoc(base_url, path, is_annotation=False):
        class_name = path.split('/')[-1].split('.')[0]
        # Handle inner classes (e.g., Provider.Inner)
        if path.split('/')[-1].count('.') > 1:
            class_name = '.'.join(path.split('/')[-1].split('.')[:-1])

        label = f"@{class_name}" if is_annotation else class_name
        env.variables[class_name] = create_link(f"{base_url}/{path}", label)

    spec_registry = {}

    def add_spec(base_url, path, display_label):
        spec_registry[display_label] = f"{base_url}/{path}"

    @env.macro
    def spec(label):
        url = spec_registry[label]
        return f'<a href="{url}" target="_blank">{label} &#8599;</a>'

    def add_method_javadoc(base_url, var_name, path_with_fragment, label):
        env.variables[var_name] = f'<a href="{base_url}/{path_with_fragment}" target="_blank">{label} &#8599;</a>'

    # instancio-core
    add_class_javadoc(javadoc_base_core, "Assign.html")
    add_class_javadoc(javadoc_base_core, "Assignment.html")
    add_class_javadoc(javadoc_base_core, "Instancio.html")
    add_class_javadoc(javadoc_base_core, "InstancioApi.html")
    add_class_javadoc(javadoc_base_core, "Model.html")
    add_class_javadoc(javadoc_base_core, "OnCompleteCallback.html")
    add_class_javadoc(javadoc_base_core, "Random.html")
    add_class_javadoc(javadoc_base_core, "Select.html")
    add_class_javadoc(javadoc_base_core, "Selector.html")
    add_class_javadoc(javadoc_base_core, "SelectorGroup.html")
    add_class_javadoc(javadoc_base_core, "TargetSelector.html")
    add_class_javadoc(javadoc_base_core, "When.html")
    add_class_javadoc(javadoc_base_core, "feed/Feed.html")
    add_class_javadoc(javadoc_base_core, "feed/FeedSpec.html")
    add_class_javadoc(javadoc_base_core, "generator/AfterGenerate.html")
    add_class_javadoc(javadoc_base_core, "generator/Generator.html")
    add_class_javadoc(javadoc_base_core, "generator/GeneratorContext.html")
    add_class_javadoc(javadoc_base_core, "generator/Hints.html")
    add_class_javadoc(javadoc_base_core, "generator/ValueSpec.html")
    add_class_javadoc(javadoc_base_core, "generator/hints/ArrayHint.html")
    add_class_javadoc(javadoc_base_core, "generator/hints/CollectionHint.html")
    add_class_javadoc(javadoc_base_core, "generator/hints/MapHint.html")
    add_class_javadoc(javadoc_base_core, "generators/Generators.html")
    add_class_javadoc(javadoc_base_core, "settings/FillType.html")
    add_class_javadoc(javadoc_base_core, "settings/Keys.html")
    add_class_javadoc(javadoc_base_core, "settings/Mode.html")
    add_class_javadoc(javadoc_base_core, "settings/SettingKey.html")
    add_class_javadoc(javadoc_base_core, "settings/Settings.html")
    add_class_javadoc(javadoc_base_core, "spi/InstancioServiceProvider.GeneratorProvider.html")
    add_class_javadoc(javadoc_base_core, "spi/InstancioServiceProvider.html")

    # Specs
    add_spec(javadoc_base_core, "generator/specs/ArrayGeneratorSpec.html", "array()")
    add_spec(javadoc_base_core, "generator/specs/BigDecimalGeneratorSpec.html", "bigDecimal()")
    add_spec(javadoc_base_core, "generator/specs/BooleanGeneratorSpec.html", "booleans()")
    add_spec(javadoc_base_core, "generator/specs/CharacterGeneratorSpec.html", "chars()")
    add_spec(javadoc_base_core, "generator/specs/CollectionGeneratorSpec.html", "collection()")
    add_spec(javadoc_base_core, "generator/specs/CoordinateGeneratorSpec.html", "coordinate()")
    add_spec(javadoc_base_core, "generator/specs/CreditCardGeneratorSpec.html", "creditCard()")
    add_spec(javadoc_base_core, "generator/specs/CsvGeneratorSpec.html", "csv()")
    add_spec(javadoc_base_core, "generator/specs/CurrencyGeneratorSpec.html", "currency()")
    add_spec(javadoc_base_core, "generator/specs/DurationGeneratorSpec.html", "duration()")
    add_spec(javadoc_base_core, "generator/specs/EanGeneratorSpec.html", "ean()")
    add_spec(javadoc_base_core, "generator/specs/EmailGeneratorSpec.html", "email()")
    add_spec(javadoc_base_core, "generator/specs/EmitGeneratorSpec.html", "emit()")
    add_spec(javadoc_base_core, "generator/specs/EnumGeneratorSpec.html", "enumOf(Class&lt;E&gt;)")
    add_spec(javadoc_base_core, "generator/specs/EnumSetGeneratorSpec.html", "enumSet(Class&lt;E&gt;)")
    add_spec(javadoc_base_core, "generator/specs/HashGeneratorSpec.html", "hash()")
    add_spec(javadoc_base_core, "generator/specs/InstantGeneratorSpec.html", "instant()")
    add_spec(javadoc_base_core, "generator/specs/IntervalSpec.html", "intervalStarting(T)")
    add_spec(javadoc_base_core, "generator/specs/Ip4GeneratorSpec.html", "ip4()")
    add_spec(javadoc_base_core, "generator/specs/IsbnGeneratorSpec.html", "isbn()")
    add_spec(javadoc_base_core, "generator/specs/LocalDateTimeGeneratorSpec.html", "localDateTime()")
    add_spec(javadoc_base_core, "generator/specs/LocalTimeGeneratorSpec.html", "localTime()")
    add_spec(javadoc_base_core, "generator/specs/LoremIpsumGeneratorSpec.html", "loremIpsum()")
    add_spec(javadoc_base_core, "generator/specs/LuhnGeneratorSpec.html", "luhn()")
    add_spec(javadoc_base_core, "generator/specs/MapGeneratorSpec.html", "map()")
    add_spec(javadoc_base_core, "generator/specs/Mod10GeneratorSpec.html", "mod10()")
    add_spec(javadoc_base_core, "generator/specs/Mod11GeneratorSpec.html", "mod11()")
    add_spec(javadoc_base_core, "generator/specs/MonthDayGeneratorSpec.html", "monthDay()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "atomicInteger()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "atomicLong()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "bigInteger()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "bytes()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "doubles()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "floats()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "ints()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "longs()")
    add_spec(javadoc_base_core, "generator/specs/NumberGeneratorSpec.html", "shorts()")
    add_spec(javadoc_base_core, "generator/specs/OffsetDateTimeGeneratorSpec.html", "offsetDateTime()")
    add_spec(javadoc_base_core, "generator/specs/OffsetTimeGeneratorSpec.html", "offsetTime()")
    add_spec(javadoc_base_core, "generator/specs/OneOfArrayGeneratorSpec.html", "oneOf(T...)")
    add_spec(javadoc_base_core, "generator/specs/OneOfCollectionGeneratorSpec.html", "oneOf(Collection&lt;T&gt;)")
    add_spec(javadoc_base_core, "generator/specs/OptionalGeneratorSpec.html", "optional()")
    add_spec(javadoc_base_core, "generator/specs/PathGeneratorSpec.html", "file()")
    add_spec(javadoc_base_core, "generator/specs/PathGeneratorSpec.html", "path()")
    add_spec(javadoc_base_core, "generator/specs/PeriodGeneratorSpec.html", "period()")
    add_spec(javadoc_base_core, "generator/specs/ShuffleSpec.html", "shuffle(Collection&lt;T&gt;)")
    add_spec(javadoc_base_core, "generator/specs/ShuffleSpec.html", "shuffle(T...)")
    add_spec(javadoc_base_core, "generator/specs/StringGeneratorSpec.html", "string()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "calendar()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "date()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "localDate()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "sqlDate()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "timestamp()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "year()")
    add_spec(javadoc_base_core, "generator/specs/TemporalGeneratorSpec.html", "yearMonth()")
    add_spec(javadoc_base_core, "generator/specs/TextPatternGeneratorSpec.html", "pattern(String)")
    add_spec(javadoc_base_core, "generator/specs/URIGeneratorSpec.html", "uri()")
    add_spec(javadoc_base_core, "generator/specs/URLGeneratorSpec.html", "url()")
    add_spec(javadoc_base_core, "generator/specs/UUIDStringGeneratorSpec.html", "uuid()")
    add_spec(javadoc_base_core, "generator/specs/WordGeneratorSpec.html", "word()")
    add_spec(javadoc_base_core, "generator/specs/WordTemplateGeneratorSpec.html", "wordTemplate(String)")
    add_spec(javadoc_base_core, "generator/specs/ZonedDateTimeGeneratorSpec.html", "zonedDateTime()")
    add_spec(javadoc_base_core, "generator/specs/bra/CnpjGeneratorSpec.html", "cnpj()")
    add_spec(javadoc_base_core, "generator/specs/bra/CpfGeneratorSpec.html", "cpf()")
    add_spec(javadoc_base_core, "generator/specs/bra/TituloEleitoralGeneratorSpec.html", "tituloEleitoral()")
    add_spec(javadoc_base_core, "generator/specs/can/SinGeneratorSpec.html", "sin()")
    add_spec(javadoc_base_core, "generator/specs/pol/NipGeneratorSpec.html", "nip()")
    add_spec(javadoc_base_core, "generator/specs/pol/PeselGeneratorSpec.html", "pesel()")
    add_spec(javadoc_base_core, "generator/specs/pol/RegonGeneratorSpec.html", "regon()")
    add_spec(javadoc_base_core, "generator/specs/rus/InnGeneratorSpec.html", "inn()")
    add_spec(javadoc_base_core, "generator/specs/usa/SsnGeneratorSpec.html", "ssn()")

    # API methods
    add_method_javadoc(javadoc_base_core, "withFillType",
                       "InstancioObjectApi.html#withFillType(org.instancio.settings.FillType)",
                       "withFillType(FillType)")
    add_method_javadoc(javadoc_base_core, "withMaxDepth", "InstancioApi.html#withMaxDepth(int)", "withMaxDepth(int)")
    add_method_javadoc(javadoc_base_core, "withNullable",
                       "InstancioApi.html#withNullable(org.instancio.TargetSelector)", "withNullable(TargetSelector)")
    add_method_javadoc(javadoc_base_core, "withSeed", "InstancioApi.html#withSeed(long)", "withSeed(long)")
    add_method_javadoc(javadoc_base_core, "withSetting",
                       "InstancioApi.html#withSetting(org.instancio.settings.SettingKey,V)",
                       "withSetting(SettingKey&lt;T&gt;, T)")
    add_method_javadoc(javadoc_base_core, "withSettings",
                       "InstancioApi.html#withSettings(org.instancio.settings.Settings)", "withSettings(Settings)")

    # instancio-junit
    add_class_javadoc(javadoc_base_junit, "Given.html", is_annotation=True)
    add_class_javadoc(javadoc_base_junit, "InstancioExtension.html")
    add_class_javadoc(javadoc_base_junit, "InstancioSource.html", is_annotation=True)
    add_class_javadoc(javadoc_base_junit, "Seed.html", is_annotation=True)
    add_class_javadoc(javadoc_base_junit, "WithSettings.html", is_annotation=True)

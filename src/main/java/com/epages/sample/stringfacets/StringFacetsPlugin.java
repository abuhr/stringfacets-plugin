package com.epages.sample.stringfacets;

import com.epages.commons.plugin.Plugin;
import com.epages.product.attribute.entry.CustomAttributeMapEntry;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.reader.AttributeReader;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public final class StringFacetsPlugin extends AbstractModule implements Plugin {
    @Override
    protected void configure() {
        bind(new TypeLiteral<AttributeReader<CustomAttributeValue, CustomAttributeMapEntry>>() {}).annotatedWith(Names.named("partner-locstring")).to(LocalizedStringReader.class);

        Multibinder<AttributeReader<?, ?>> attributeBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<AttributeReader<?, ?>>() {});
        attributeBinder.addBinding().to(LocalizedStringReader.class);
    }
}
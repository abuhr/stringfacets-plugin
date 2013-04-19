package com.epages.sample.stringfacets;

import com.epages.commons.plugin.Plugin;
import com.epages.index.domain.solrj.doc.DocumentAppender;
import com.epages.product.attribute.entry.CustomAttributeMapEntry;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.attribute.entry.SearchFilterAttributeType;
import com.epages.product.reader.AttributeReader;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public final class StringFacetsPlugin extends AbstractModule implements Plugin {
    @Override
    protected void configure() {
        bind(new TypeLiteral<AttributeReader<CustomAttributeValue, CustomAttributeMapEntry>>() {}).annotatedWith(Names.named("partner-locstring")).to(LocalizedStringReader.class);

        Multibinder<AttributeReader<?, ?>> readerBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<AttributeReader<?, ?>>() {});
        readerBinder.addBinding().to(LocalizedStringReader.class);
        readerBinder.addBinding().to(StringReader.class);

        Multibinder<DocumentAppender> allVariations = Multibinder.newSetBinder(binder(), DocumentAppender.class, Names.named("allVariations"));
        allVariations.addBinding().to(StringAttributeAppender.class);

        Multibinder<SearchFilterAttributeType> attributeBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<SearchFilterAttributeType>() {});
        attributeBinder.addBinding().toInstance(CustomSearchFilterAttributeTypeExtensions.String);
        attributeBinder.addBinding().toInstance(CustomSearchFilterAttributeTypeExtensions.LocalizedString);
    }
}
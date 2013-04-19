package com.epages.sample.stringfacets;

import static com.epages.index.entities.solrj.doc.SolrFieldNames.PREDEFVALUES;

import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.solr.common.SolrInputDocument;

import com.epages.index.domain.solrj.doc.DocumentAppender;
import com.epages.index.domain.solrj.doc.DocumentValueAppender;
import com.epages.product.IProduct;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.attribute.entry.LocalizedKey;
import com.google.inject.name.Named;

public class StringAttributeAppender implements DocumentAppender {

    private final DocumentValueAppender appender;

    @Inject
    public StringAttributeAppender(@Named("multi") DocumentValueAppender appender) {
        this.appender = appender;
    }

    @Override
    public void append(SolrInputDocument doc, IProduct product) {
        for (Entry<LocalizedKey, CustomAttributeValue> entry : product.getAllAttributes(LocalizedKey.class, CustomAttributeValue.class)) {
            CustomAttributeValue value = entry.getValue();
            if (CustomSearchFilterAttributeTypeExtensions.String.equals(value.getType()) 
                    || CustomSearchFilterAttributeTypeExtensions.LocalizedString.equals(value.getType())) {
                if (value.isSearchFilter()) {
                    this.appender.append(value.getFieldName(), value.getValue(), doc);
                }
                this.appender.append(PREDEFVALUES.getWithPrefix(entry.getKey().getLang()), value.getValue(), doc);
            }
        }
    }

}

package com.epages.sample.stringfacets;

import com.epages.index.entities.solrj.doc.SolrFieldNames;
import com.epages.product.attribute.entry.SearchFilterAttributeType;


public enum CustomSearchFilterAttributeTypeExtensions implements SearchFilterAttributeType {

    String {
        @Override 
        public String getSolrFieldName() {return SolrFieldNames.STRING.get();}
    },
    LocalizedString {
        @Override 
        public String getSolrFieldName() {return SolrFieldNames.STRING.get();}
    };

    @Override
    public String getEp6AttributeType() {
        return this.name();
    }

}

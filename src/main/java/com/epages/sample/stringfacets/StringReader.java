package com.epages.sample.stringfacets;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.springframework.jdbc.core.RowMapper;

import com.epages.dao.ProductReaderPreparedStatementSetter;
import com.epages.datasource.api.DataSourceLookup;
import com.epages.metadata.JdbcCursorItemReader4EPJ;
import com.epages.product.Product;
import com.epages.product.attribute.entry.AttributeKey;
import com.epages.product.attribute.entry.AttributeKeyBuilder;
import com.epages.product.attribute.entry.CustomAttributeMapEntry;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.reader.AttributeValidator;
import com.epages.product.reader.BasePeekableReader;

final class StringReader extends BasePeekableReader<CustomAttributeValue, CustomAttributeMapEntry> {

    @Inject
    public StringReader(AttributeValidator validator, DataSourceLookup dataSourceLookup) {
        super(validator, new Reader(dataSourceLookup));
    }

    @Override
    public void addToProduct(Product product, CustomAttributeMapEntry attribute) {
        product.addAttribute(attribute);
    }
    
    static final class Reader extends JdbcCursorItemReader4EPJ<CustomAttributeMapEntry> {

        private static final RowMapper<CustomAttributeMapEntry> ROW_MAPPER =  new RowMapper<CustomAttributeMapEntry>() {

            @Override
            public CustomAttributeMapEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttributeKey key = new AttributeKeyBuilder(rs.getInt("main_productid"), rs.getString("attributekey")).withVariationId(getVariationId(rs)).localised(rs.getString("langcode")).build();
                CustomAttributeValue attributeValue = new CustomAttributeValue(rs.getString("attributename"), CustomSearchFilterAttributeTypeExtensions.valueOf(rs.getString("attributetype")), rs.getString("langcode"));
                attributeValue.setAttributeValue(rs.getString("attributevalue"), rs.getInt("attributeposition"));
                attributeValue.setIssearchfilter(1);
                return new CustomAttributeMapEntry(key, attributeValue);
            }
        };

        public static final String SQL = new StringBuilder()
            .append(" SELECT l.code2 as langcode, pa.value, aliasvalue.alias, a.type as attributetype, ")
            .append(" COALESCE(pa.value, aliasvalue.alias) as attributevalue, ")
            .append(" name.value, aliasname.alias as attributekey, aliasvalue.position as attributeposition, ")
            .append(" COALESCE(name.value, aliasname.alias) as attributename,  p.superproductid, p.productid, ")
            .append(" COALESCE(p.superproductid, p.productid) as main_productid ")
            .append(" FROM product p ")
            .append(" JOIN shoplanguage sl ON p.shopid = sl.shopid ")
            .append(" JOIN stringattribute pa ON pa.objectid = p.productid ")
            .append(" JOIN attribute a ON pa.attributeid = a.attributeid AND type = 'String' AND a.isuserdefined = 1 ")
            .append(" JOIN object aliasvalue ON pa.attributeid = aliasvalue.objectid ")
            .append(" JOIN object aliasname ON aliasname.objectid=pa.attributeid ")
            .append(" LEFT JOIN (localizedstringattribute name JOIN object o ON name.attributeid = o.objectid AND o.alias = 'Name') ON pa.attributeid=name.objectid AND name.languageid = sl.languageid ")
            .append(" JOIN language l ON sl.languageid = l.languageid ")
            .append(" WHERE p.shopid = ? %s ")
            .append(" AND (a.isvisible = 1) ")
            .append(" ORDER BY main_productid ").toString();


        public static final String SHOP_SQL = String.format(SQL, "");

        public static final String PRODUCT_SQL = String.format(SQL, " AND COALESCE(p.superproductid, p.productid) = ?");

        public Reader(DataSourceLookup dataSourceLookup) {
            super(dataSourceLookup, ROW_MAPPER);
        }
        
        @Override
        public void setReader(String dsName, ProductReaderPreparedStatementSetter pstSetter) {
            super.setReader(dsName, pstSetter, getSQL(pstSetter));
        }

        private static String getSQL(ProductReaderPreparedStatementSetter pstSetter) {
            if (pstSetter.getProductId() > 0) {
                return PRODUCT_SQL;
            }
            return SHOP_SQL;
        }
    }

}

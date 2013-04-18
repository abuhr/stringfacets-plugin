package com.epages.sample.stringfacets;


import static org.junit.Assert.assertEquals;

import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.epages.commons.plugin.PluginServiceLoader;
import com.epages.dao.ProductReaderPreparedStatementSetter;
import com.epages.product.DemoShop;
import com.epages.product.attribute.entry.CustomAttributeMapEntry;
import com.epages.product.attribute.entry.CustomAttributeValue;
import com.epages.product.reader.AttributeReader;
import com.google.inject.Inject;

@RunWith(MockitoJUnitRunner.class)
public class LocalizedStringReaderTest {

    private static final PluginServiceLoader PLUGIN_SERVICE_LOADER = PluginServiceLoader.getInstance();

    @Inject
    @Named("partner-locstring")
    private AttributeReader<CustomAttributeValue, CustomAttributeMapEntry> readerSUT;

    @Inject
    private DemoShop demoShop;

    @Before
    public void setUp() {
        PLUGIN_SERVICE_LOADER.injectMembers(this);
    }

    @After
    public void tearDown() {
        this.readerSUT.close();
    }

    @Test
    public void testDemoShopStringAttributes() throws Exception {
        readerSUT.setReader(DemoShop.STORENAME, new ProductReaderPreparedStatementSetter(demoShop.shopId));
        readerSUT.open();
        int i = 0;
        while (readerSUT.read() != null) {
            i++;
        }
        readerSUT.close();
        assertEquals(80, i);
    }

}

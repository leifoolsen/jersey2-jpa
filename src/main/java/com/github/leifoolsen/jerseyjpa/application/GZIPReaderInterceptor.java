package com.github.leifoolsen.jerseyjpa.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class GZIPReaderInterceptor implements ReaderInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {

        MultivaluedMap<String,String> headers = context.getHeaders();
        List<String> contentEncoding = headers.get("Content-Encoding");

        if(contentEncoding!= null && contentEncoding.contains("gzip")) {
            logger.debug("Decompressing GZIP");

            final InputStream originalInputStream = context.getInputStream();
            context.setInputStream(new GZIPInputStream(originalInputStream));
        }
        return context.proceed();
    }
}

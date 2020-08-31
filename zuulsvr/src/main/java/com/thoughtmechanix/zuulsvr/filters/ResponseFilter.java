package com.thoughtmechanix.zuulsvr.filters;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zipkin2.Span;

@Component
public class ResponseFilter extends ZuulFilter {
    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    Tracer tracer;

    @Override
    public String filterType() {
        return FilterUtils.POST_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        logger.debug("Adding the correlation Id to the outbound headers. {}", filterUtils.getCorrelationId());
        TraceContext context = tracer.currentSpan().context();

        ctx.getResponse().addHeader(FilterUtils.CORRELATION_ID,
               context.spanIdString());
        logger.debug("Completing outgoing request for {}.", ctx.getRequest().getRequestURI());
        return null;
    }
}

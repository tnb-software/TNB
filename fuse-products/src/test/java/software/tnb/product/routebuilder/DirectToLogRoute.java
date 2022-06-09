package software.tnb.product.routebuilder;

import org.apache.camel.builder.RouteBuilder;

public class DirectToLogRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:start").log("${body}");
    }
}

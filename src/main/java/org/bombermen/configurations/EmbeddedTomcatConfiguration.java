package org.bombermen.configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.catalina.connector.Connector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EmbeddedTomcatConfiguration {
//
//    @Value("${server.port}")
//    private String serverPort;
//
//    @Value("${server.additionalPorts:null}")
//    private String additionalPorts;
//
//    @Bean
//    public TomcatServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//        Connector[] additionalConnectors = this.additionalConnector();
//        if (additionalConnectors != null && additionalConnectors.length > 0) {
//            tomcat.addAdditionalTomcatConnectors(additionalConnectors);
//        }
//        return tomcat;
//    }
//
//    private Connector[] additionalConnector() {
//        if (StringUtils.isBlank(this.additionalPorts)) {
//            return null;
//        }
//        Set<String> defaultPorts = Sets.newHashSet(this.serverPort);
//        String[] ports = new String[] {additionalPorts};
//        List<Connector> result = new ArrayList<>();
//        for (String port : ports) {
//            if (!defaultPorts.contains(port)) {
//                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//                connector.setScheme("http");
//                connector.setPort(Integer.valueOf(port));
//                result.add(connector);
//            }
//        }
//        return result.toArray(new Connector[] {});
//    }
}
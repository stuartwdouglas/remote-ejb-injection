package org.wildfly.remoteejbinjection.jar;

import java.net.URL;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.remoteejbinjection.ejbs.StatefulEjbInterface;
import org.wildfly.remoteejbinjection.ejbs.StatelessEjb;
import org.wildfly.remoteejbinjection.ejbs.StatelessEjbInterface;
import org.wildfly.remoteejbinjection.utils.HttpUtils;
import org.wildfly.remoteejbinjection.utils.ShrinkWrapUtils;

@RunWith(Arquillian.class)
@RunAsClient
public class RemoteEjbInjectionJarTestCase {


    @Deployment(testable = false, name = "test.jar")
    public static JavaArchive deployRemoteEjbArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(StatelessEjb.class.getPackage());
    }

    @Deployment(name = "test1.war", testable = false)
    public static WebArchive deployTestApp1() {

        return ShrinkWrap.create(WebArchive.class, "test1.war")
                .addClasses(StatelessServlet.class, StatefulServlet.class)
                .addClasses(StatefulEjbInterface.class, StatelessEjbInterface.class)
                .addAsResource( new StringAsset(""), "WEB-INF/beans.xml")
                .addAsResource(RemoteEjbInjectionJarTestCase.class.getPackage(), "remote-ejb-injection1.xml", "remote-ejb-injection.xml")
                .addAsLibrary(ShrinkWrapUtils.createLibJar());
    }

    @Deployment(name = "test2.war", testable = false)
    public static WebArchive deployTestApp2() {

        return ShrinkWrap.create(WebArchive.class, "test2.war")
                .addClasses(StatelessServlet.class, StatefulServlet.class)
                .addClasses(StatefulEjbInterface.class, StatelessEjbInterface.class)
                .addAsResource( new StringAsset(""), "WEB-INF/beans.xml")
                .addAsResource(RemoteEjbInjectionJarTestCase.class.getPackage(), "remote-ejb-injection2.xml", "remote-ejb-injection.xml")
                .addAsLibrary(ShrinkWrapUtils.createLibJar());
    }

    @ArquillianResource
    @OperateOnDeployment("test1.war")
    private URL url1;

    @ArquillianResource
    @OperateOnDeployment("test2.war")
    private URL url2;

    private String performStatelessCall(URL url, String name) throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url + "/stateless");
        httpGet.addHeader("name", name);
        HttpResponse result = client.execute(httpGet);
        return HttpUtils.getContent(result);
    }

    @Test
    public void testStatelessWithProviderURI() throws Exception {
        Assert.assertEquals("Hello stu", performStatelessCall(url1, "stu"));
    }

    @Test
    public void testStatelessWithoutProviderURI() throws Exception {
        Assert.assertEquals("Hello stu", performStatelessCall(url2, "stu"));
    }

    private String performStatefulCall(URL url) throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url + "/stateful");
        HttpResponse result = client.execute(httpGet);
        return HttpUtils.getContent(result);
    }

    @Test
    public void testStatefulWithProviderURI() throws Exception {
        Assert.assertEquals("ok", performStatefulCall(url2));
    }

    @Test
    public void testStatefulWithoutProviderURI() throws Exception {
        Assert.assertEquals("ok", performStatefulCall(url2));
    }

}

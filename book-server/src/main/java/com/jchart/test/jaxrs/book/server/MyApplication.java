package com.jchart.test.jaxrs.book.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@ApplicationPath("/api")
// TODO -- NOT IN USE, specified in web.xml
public class MyApplication extends Application {

	/**
	 * add resources
	 */
	@Override
	public Set<Class<?>> getClasses() {
        Set<Class<?>> retval = new HashSet<Class<?>>();
        retval.add(BooksResource.class);
		return retval;
	}

	/**
	 * register providers
	 */
	@Override
    public Set<Object> getSingletons() {
        HashSet<Object> singletons = new HashSet<Object>();
        singletons.add(new JacksonJaxbJsonProvider());
        return singletons;
    }

}

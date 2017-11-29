package org.wildfly.remoteejbinjection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Stuart Douglas
 */
class RemoteEjbInjectionParser {

    static final String PROVIDER_URI = "provider-uri";
    static final String MODULE = "module";
    static final String APP = "app";
    static final String DISTINCT = "distinct";
    static final String EJB = "ejb";
    static final String INTERFACE_CLASS = "interface-class";
    static final String EJB_NAME = "ejb-name";
    static final String STATEFUL = "stateful";

    static List<RemoteEjbConfig> parse(URL url) throws XMLStreamException, IOException, ClassNotFoundException {
        try (InputStream inputStream = url.openStream()) {
            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            reader.require(XMLStreamConstants.START_DOCUMENT, null, null);

            while (reader.hasNext()) {
                switch (reader.nextTag()) {
                    case XMLStreamConstants.START_ELEMENT: {
                        String element = reader.getLocalName();
                        switch (element) {
                            case "ejb-injection": {
                                return parseEjbInjection(reader);
                            }
                            default: {
                                throw unexpectedElement(reader);
                            }
                        }
                    }
                    default: {
                        throw unexpectedContent(reader);
                    }
                }
            }
            throw unexpectedEndOfDocument(reader);
        }
    }


    private static List<RemoteEjbConfig> parseEjbInjection(final XMLStreamReader reader) throws XMLStreamException, ClassNotFoundException {

        List<RemoteEjbConfig> configs = new ArrayList<>();

        requireNoAttributes(reader);

        while (reader.hasNext()) {
            switch (reader.nextTag()) {
                case XMLStreamConstants.END_ELEMENT: {
                    return configs;
                }
                case XMLStreamConstants.START_ELEMENT: {
                    String element = reader.getLocalName();
                    switch (element) {
                        case "ejbs" : {
                            configs.add(parseEjbsInjection(reader));
                            break;
                        }
                        default: {
                            throw unexpectedElement(reader);
                        }
                    }
                    break;
                }
                default: {
                    throw unexpectedContent(reader);
                }
            }
        }
        throw unexpectedEndOfDocument(reader);
    }

    private static RemoteEjbConfig parseEjbsInjection(final XMLStreamReader reader) throws XMLStreamException, ClassNotFoundException {

        String app = null;
        String providerUri = null;
        String module = null;
        String distinct = null;
        List<RemoteEjbConfig.RemoteEjb> remoteEjbs = new ArrayList<>();

        Set<String> required = new HashSet<>();
        required.add(MODULE);
        // parse the permissions required.
        for(int i = 0; i < reader.getAttributeCount(); ++i) {
            String name = reader.getAttributeLocalName(i);
            required.remove(name);
            String value = reader.getAttributeValue(i);
            switch (name) {
                case MODULE: {
                    module = value;
                    break;
                }
                case APP: {
                    app = value;
                    break;
                }
                case DISTINCT: {
                    distinct = value;
                    break;
                }
                case PROVIDER_URI: {
                    providerUri = value;
                    break;
                }
            }
        }
        if(!required.isEmpty()) {
            throw missingRequiredAttributes(reader, required);
        }

        // parse the permissions sub-elements.
        while (reader.hasNext()) {
            switch (reader.nextTag()) {
                case XMLStreamConstants.END_ELEMENT: {
                    return new RemoteEjbConfig(providerUri, app, module, distinct, remoteEjbs);
                }
                case XMLStreamConstants.START_ELEMENT: {
                    String element = reader.getLocalName();
                    switch (element) {
                        case EJB: {
                            remoteEjbs.add(parseEjb(reader));
                            break;
                        }
                        default: {
                            throw unexpectedElement(reader);
                        }
                    }
                    break;
                }
                default: {
                    throw unexpectedContent(reader);
                }
            }
        }
        throw unexpectedEndOfDocument(reader);
    }

    private static RemoteEjbConfig.RemoteEjb parseEjb(XMLStreamReader reader) throws XMLStreamException, ClassNotFoundException {

        String interfaceClass = null;
        String ejbName = null;
        boolean stateful = false;
        List<RemoteEjbConfig.RemoteEjb> remoteEjbs = new ArrayList<>();

        Set<String> required = new HashSet<>();
        required.add(INTERFACE_CLASS);
        required.add(EJB_NAME);
        // parse the permissions required.
        for(int i = 0; i < reader.getAttributeCount(); ++i) {
            String name = reader.getAttributeLocalName(i);
            required.remove(name);
            String value = reader.getAttributeValue(i);
            switch (name) {
                case INTERFACE_CLASS: {
                    interfaceClass = value;
                    break;
                }
                case EJB_NAME: {
                    ejbName = value;
                    break;
                }
                case STATEFUL: {
                    stateful = Boolean.parseBoolean(value);
                    break;
                }
            }
        }
        if(!required.isEmpty()) {
            throw missingRequiredAttributes(reader, required);
        }

        // parse the permissions sub-elements.
        while (reader.hasNext()) {
            switch (reader.nextTag()) {
                case XMLStreamConstants.END_ELEMENT: {
                    return new RemoteEjbConfig.RemoteEjb(Thread.currentThread().getContextClassLoader().loadClass(interfaceClass), ejbName, stateful);
                }
                case XMLStreamConstants.START_ELEMENT: {
                    String element = reader.getLocalName();
                    switch (element) {
                        case EJB: {
                            remoteEjbs.add(parseEjb(reader));
                            break;
                        }
                        default: {
                            throw unexpectedElement(reader);
                        }
                    }
                    break;
                }
                default: {
                    throw unexpectedContent(reader);
                }
            }
        }
        throw unexpectedEndOfDocument(reader);
    }

    private static XMLStreamException unexpectedContent(final XMLStreamReader reader) {
        final String kind;
        switch (reader.getEventType()) {
            case XMLStreamConstants.ATTRIBUTE:
                kind = "attribute";
                break;
            case XMLStreamConstants.CDATA:
                kind = "cdata";
                break;
            case XMLStreamConstants.CHARACTERS:
                kind = "characters";
                break;
            case XMLStreamConstants.COMMENT:
                kind = "comment";
                break;
            case XMLStreamConstants.DTD:
                kind = "dtd";
                break;
            case XMLStreamConstants.END_DOCUMENT:
                kind = "document end";
                break;
            case XMLStreamConstants.END_ELEMENT:
                kind = "element end";
                break;
            case XMLStreamConstants.ENTITY_DECLARATION:
                kind = "entity declaration";
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                kind = "entity ref";
                break;
            case XMLStreamConstants.NAMESPACE:
                kind = "namespace";
                break;
            case XMLStreamConstants.NOTATION_DECLARATION:
                kind = "notation declaration";
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                kind = "processing instruction";
                break;
            case XMLStreamConstants.SPACE:
                kind = "whitespace";
                break;
            case XMLStreamConstants.START_DOCUMENT:
                kind = "document start";
                break;
            case XMLStreamConstants.START_ELEMENT:
                kind = "element start";
                break;
            default:
                kind = "unknown";
                break;
        }
        return new XMLStreamException("Unexpected content type " + kind + " at " + reader.getLocation());
    }

    /**
     * Gets an exception reporting an unexpected end of XML document.
     *
     * @param reader a reference to the stream reader.
     * @return the constructed {@link javax.xml.stream.XMLStreamException}.
     */
    private static XMLStreamException unexpectedEndOfDocument(final XMLStreamReader reader) {
        return new XMLStreamException("Unexpected end of document at " + reader.getLocation());
    }

    /**
     * Gets an exception reporting an unexpected XML element.
     *
     * @param reader a reference to the stream reader.
     * @return the constructed {@link javax.xml.stream.XMLStreamException}.
     */
    private static XMLStreamException unexpectedElement(final XMLStreamReader reader) {
        return new XMLStreamException("Unexpected element " + reader.getName() + " at " + reader.getLocation());
    }

    /**
     * Gets an exception reporting an unexpected XML attribute.
     *
     * @param reader a reference to the stream reader.
     * @param index the attribute index.
     * @return the constructed {@link javax.xml.stream.XMLStreamException}.
     */
    private static XMLStreamException unexpectedAttribute(final XMLStreamReader reader, final int index) {
        return new XMLStreamException("Unexpected attribute " + reader.getAttributeName(index) + " at " + reader.getLocation());
    }

    /**
     * Checks that the current element has no attributes, throwing an {@link javax.xml.stream.XMLStreamException} if one is found.
     *
     * @param reader a reference to the stream reader.
     * @throws {@link javax.xml.stream.XMLStreamException} if an error occurs.
     */
    private static void requireNoAttributes(final XMLStreamReader reader) throws XMLStreamException {
        if (reader.getAttributeCount() > 0) {
            throw unexpectedAttribute(reader, 0);
        }
    }

    /**
     * Gets an exception reporting missing required XML attribute(s).
     *
     * @param reader a reference to the stream reader
     * @param required a set of enums whose toString method returns the attribute name.
     * @return the constructed {@link javax.xml.stream.XMLStreamException}.
     */
    private static XMLStreamException missingRequiredAttributes(final XMLStreamReader reader, final Set<?> required) {
        final StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = required.iterator();
        while (iterator.hasNext()) {
            final Object o = iterator.next();
            builder.append(o.toString());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return new XMLStreamException("Missing required attribute " + builder + " at " + reader.getLocation());
    }

}

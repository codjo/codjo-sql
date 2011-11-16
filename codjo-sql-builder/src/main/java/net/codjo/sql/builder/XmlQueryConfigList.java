/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import net.codjo.xml.XmlException;
import net.codjo.xml.fast.ClientContentHandler;
import net.codjo.xml.fast.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.InputSource;
/**
 * Permet de construire une liste de QueryConfig à partir d'un fichier xml.
 */
public class XmlQueryConfigList {
    private Map<String, QueryConfig> configMap = new HashMap<String, QueryConfig>();


    public XmlQueryConfigList() {
    }


    public static XmlQueryConfigList newXmlQueryConfigList(InputStream stream)
          throws IOException, XmlException {
        XmlParser xmlParser = new XmlParser();
        ListBuilder listBuilder = new ListBuilder();
        xmlParser.parse(new InputSource(stream), listBuilder);
        return listBuilder.getList();
    }


    public QueryConfig getConfig(String configId) {
        return configMap.get(configId);
    }


    public int size() {
        return configMap.size();
    }


    void add(String id, QueryConfig config) {
        configMap.put(id, config);
    }


    /**
     * Handler permettant de construire un XmlQueryConfigList.
     */
    private static class ListBuilder implements ClientContentHandler {
        private XmlQueryConfigList list = new XmlQueryConfigList();
        private String id;
        private DefaultQueryConfig queryConfig;
        private JoinKey joinKey;
        private JoinKey.Part part;


        public XmlQueryConfigList getList() {
            return list;
        }


        public void startElement(String name, Map attributes) {
            if ("query-config".equals(name)) {
                queryConfig = new DefaultQueryConfig();
                id = (String)attributes.get("id");
                queryConfig.setRootTableName((String)attributes.get("root"));
            }
            else if ("join-key".equals(name)) {
                joinKey =
                      new JoinKey((String)attributes.get("left"),
                                  convert((String)attributes.get("type")),
                                  (String)attributes.get("right"));
            }
            else if ("part".equals(name)) {
                part =
                      new JoinKey.Part((String)attributes.get("left"),
                                       (String)attributes.get("operator"),
                                       (String)attributes.get("right"));
            }
        }


        public void endElement(String name, String value) {
            if ("query-config".equals(name)) {
                list.add(id, queryConfig);
            }
            else if ("join-key".equals(name)) {
                queryConfig.add(joinKey);
            }
            else if ("part".equals(name)) {
                joinKey.addPart(part);
            }
        }


        private JoinKey.Type convert(String joinType) {
            if ("left".equals(joinType)) {
                return JoinKey.Type.LEFT;
            }
            else if ("right".equals(joinType)) {
                return JoinKey.Type.RIGHT;
            }
            else if ("inner".equals(joinType)) {
                return JoinKey.Type.INNER;
            }
            else {
                throw new IllegalArgumentException("Type inconnu " + joinType);
            }
        }
    }
}

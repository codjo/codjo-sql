/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.sql.builder;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Classe de test de la classe DefaultQueryConfig.
 */
public class DefaultQueryConfigTest extends TestCase {
    private DefaultQueryConfig config;


    @Override
    protected void setUp() throws Exception {
        config = new DefaultQueryConfig();
    }


    public void test_getOrderByFieldsReturnsEmptyArray() throws Exception {
        assertEquals(0, config.getOrderByFields().length);
    }


    public void test_getRootExpression() throws Exception {
        assertNull("Par défaut il n'y a pas d'expression attaché à la table racine",
                   config.getRootExpression());

        JoinKeyExpression expression = new JoinKeyExpression("A=b");
        config.setRootExpression(expression);

        assertSame(expression, config.getRootExpression());
    }


    public void test_map() {
        Map<String, JoinKey> map = config.getJoinKeyMap();

        assertNotNull(map);

        try {
            map.put("cle", new JoinKey("e", JoinKey.Type.INNER, "b"));
            fail("La joinKeyMap est non modifiable.");
        }
        catch (UnsupportedOperationException ex) {
            // Ok
        }
    }


    public void test_add() {
        final JoinKey r2a = new JoinKey("R", JoinKey.Type.INNER, "A");
        final JoinKey a2b = new JoinKey("A", JoinKey.Type.INNER, "B");

        config.setRootTableName("R");
        config.add(r2a);
        config.add(a2b);

        Map map = config.getJoinKeyMap();

        assertEquals(2, map.size());
        assertSame(r2a, map.get("A"));
        assertSame(a2b, map.get("B"));
    }


    public void test_add_orphan() {
        final JoinKey r2a = new JoinKey("R", JoinKey.Type.INNER, "A");
        final JoinKey a2b = new JoinKey("A", JoinKey.Type.INNER, "B");
        final JoinKey c2d = new JoinKey("C", JoinKey.Type.INNER, "D");

        config.setRootTableName("R");
        config.add(r2a);
        config.add(a2b);

        try {
            config.add(c2d);
            fail("Doit échouer car c2d n'est pas rattaché au graphe.");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }

        Map map = config.getJoinKeyMap();

        assertEquals(2, map.size());
        assertSame(r2a, map.get("A"));
        assertSame(a2b, map.get("B"));
    }


    public void test_add_cyclic() {
        final JoinKey r2a = new JoinKey("R", JoinKey.Type.INNER, "A");
        final JoinKey a2b = new JoinKey("A", JoinKey.Type.INNER, "B");
        final JoinKey b2r = new JoinKey("B", JoinKey.Type.INNER, "R");

        config.setRootTableName("R");
        config.add(r2a);
        config.add(a2b);

        try {
            config.add(b2r);
            fail("Doit échouer car b2r produit un cycle dans le graphe.");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }

        Map map = config.getJoinKeyMap();

        assertEquals(2, map.size());
        assertSame(r2a, map.get("A"));
        assertSame(a2b, map.get("B"));
    }


    public void test_add_cyclic_linkBranch() {
        final JoinKey r2a = new JoinKey("R", JoinKey.Type.INNER, "A");
        final JoinKey a2b = new JoinKey("A", JoinKey.Type.INNER, "B");
        final JoinKey r2c = new JoinKey("R", JoinKey.Type.INNER, "C");
        final JoinKey b2c = new JoinKey("B", JoinKey.Type.INNER, "C");

        config.setRootTableName("R");
        config.add(r2a);
        config.add(a2b);
        config.add(r2c);

        try {
            config.add(b2c);
            fail("Doit échouer car b2r produit un cycle dans le graphe.");
        }
        catch (IllegalArgumentException ex) {
            // Ok
        }

        Map map = config.getJoinKeyMap();

        assertEquals(3, map.size());
        assertSame(r2a, map.get("A"));
        assertSame(a2b, map.get("B"));
        assertSame(r2c, map.get("C"));
    }
}


package cz.dynawest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
== 1 : 0,1 =================================================
SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b FROM table1 LEFT  JOIN table2 USING(id) GROUP BY(table1.id) HAVING cnt_a != 1 OR cnt_b NOT IN(0,1)
SELECT table2.id AS tcb, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b FROM table1 RIGHT JOIN table2 USING(id) GROUP BY(table2.id) HAVING cnt_a != 1
== 1 : 1 =================================================
SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b FROM table1 LEFT JOIN table2 USING(id) GROUP BY(table1.id) HAVING cnt_a != 1 OR cnt_b != 1)
SELECT table2.id AS tcb, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b FROM table1 RIGHT JOIN table2 USING(id) GROUP BY(table2.id) HAVING cnt_a != 1 OR cnt_b != 1)
== 1 : 0+ =================================================
SELECT table2.id AS tcb, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b FROM table1 RIGHT JOIN table2 USING(id) GROUP BY(table2.id) HAVING cnt_a != 1)
============================================================
== 1 : 0,1 =================================================
SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.k) AS cnt_b FROM table1 LEFT JOIN table2 USING(id) GROUP BY(table1.id) HAVING cnt_a != 1 OR cnt_b NOT IN(0,1)
SELECT table2.k AS tcb, COUNT(table1.id) AS cnt_a, COUNT(table2.k) AS cnt_b FROM table1 RIGHT JOIN table2 USING(id) GROUP BY(table2.k) HAVING cnt_a != 1
== 1 : 1 =================================================
SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.k) AS cnt_b FROM table1 LEFT JOIN table2 USING(id) GROUP BY(table1.id) HAVING cnt_a != 1 OR cnt_b != 1)
SELECT table2.k AS tcb, COUNT(table1.id) AS cnt_a, COUNT(table2.k) AS cnt_b FROM table1 RIGHT JOIN table2 USING(id) GROUP BY(table2.k) HAVING cnt_a != 1 OR cnt_b != 1)
== 1 : 0+ =================================================
SELECT table2.k AS tcb, COUNT(table1.id) AS cnt_a, COUNT(table2.k) AS cnt_b FROM table1 RIGHT JOIN table2 USING(id) GROUP BY(table2.k) HAVING cnt_a != 1)

 * @author Ondřej Žižka
 */
public class SQLIntegrityChecker {

  Logger log = Logger.getLogger( this.getClass().getName() );

  public void checkIntegrity( RelationInfo ri ){
    List<String> asQueries = getCheckSql( ri );
    for( String query : asQueries ) {
      System.out.println( query + ";" );
    }
  }



  /**
   * Vrací SQL SELECTy pro zkontrolování konzistence vztahů mezi tabulkami.
   */
  public List<String> getCheckSql( RelationInfo ri ){

    String sSql = null;
    List<String> asQueries = new ArrayList(2);

    boolean checkNonExistingParent = false;
    boolean checkNonExistingChild = false;

    // Které kontroly chceme provádět
    switch( ri.type ){
      
      case REL_1_01:
        checkNonExistingParent = true;
        // Kontrola jedinečnosti přes UNIQUE KEY na FK.
      break;

      case REL_1_1:
        // Není třeba kontrola jedinečnosti, neboť jsou spojeny stejným PK.
        checkNonExistingParent = true;
        checkNonExistingChild = true;
      break;

      // 1 : 0..*
      case REL_1_0N:
        checkNonExistingParent = true;
      break;

    }// switch



    /*
    if(false){
      
        sSql = String.format(
              "SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b " +
              "FROM `%s` AS table1 LEFT JOIN `%s` AS table2 ON table1.`%s` = table2.`%s` " +
              "GROUP BY(table1.`%s`) " +
              "HAVING cnt_a != 1 " + // V parent table není rodič.
              "OR cnt_b NOT IN(0,1)",// V child table není 0 nebo 1 childů.
              ri.parentTable, ri.childTable, ri.parentId, ri.childId, ri.parentId  );
        asQueries.add( sSql );

        sSql = String.format(
              "SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b \n" +
              "FROM `%s` AS table1 RIGHT JOIN `%s` AS table2 ON table1.`%s` = table2.`%s` \n" +
              "GROUP BY(table2.`%s`) \n" +
              "HAVING cnt_a != 1 \n",
              ri.parentTable, ri.childTable, ri.parentId, ri.childId, ri.childId  );
        asQueries.add( sSql );

    }
    if(false){
        sSql = String.format(
              "SELECT table1.id AS tca, COUNT(table1.id) AS cnt_a, COUNT(table2.id) AS cnt_b \n" +
              "FROM `%s` AS table1 RIGHT JOIN `%s` AS table2 ON table1.`%s` = table2.`%s` \n" +
              "GROUP BY(table2.`%s`) \n" +
              "HAVING cnt_a != 1 \n",
              ri.parentTable, ri.childTable, ri.parentId, ri.childId, ri.childId  );
        asQueries.add( sSql );
    }
    /**/

    if( checkNonExistingParent ){
        sSql = String.format(
              "SELECT table2.`%4$s` AS has_no_parent, table2.`%5$s` AS referenced_parent " +
              "\n FROM `%1$s` AS table1 RIGHT JOIN `%2$s` AS table2 ON table1.`%3$s` = table2.`%5$s` " +
              "\n WHERE table2.`%5$s` IS NOT NULL AND table1.`%3$s` IS NULL",
              ri.parentTable, ri.childTable, ri.parentId, ri.childId, ri.childFK );
        asQueries.add( sSql );
    }

    if( checkNonExistingChild ){
        sSql = String.format(
              "SELECT table2.`%3$s` AS has_no_child, table2.`%4$s` AS referenced_child " +
              "\n FROM `%1$s` AS table1 LEFT JOIN `%2$s` AS table2 ON table1.`%3$s` = table2.`%5$s` " +
              "\n WHERE table2.`%4$s` IS NULL",
              ri.parentTable, ri.childTable, ri.parentId, ri.childId, ri.childFK );
        asQueries.add( sSql );
    }

    return asQueries;
    
  }// getCheckSql()



}// class SQLIntegrityChecker






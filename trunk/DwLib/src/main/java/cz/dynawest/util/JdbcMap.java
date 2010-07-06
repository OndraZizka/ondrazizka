package cz.dynawest.util;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author Zizka
 */
public class JdbcMap implements Map<String,String> {
  
  private String sTable;
  private Connection conn;  
  
  class Statements {
    PreparedStatement put, get, isEmpty, containsKey, containsValue, delete, clear, keySet, values, entrySet;
  }
  private Statements statements;

  public JdbcMap( Connection conn, String sTable ) throws SQLException {
    this.conn = conn;
    this.sTable = sTable;
    this.prepareStatements();
  }

  public int size() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isEmpty() {
    try {
      ResultSet rs = this.statements.isEmpty.executeQuery();
      rs.first();
      boolean bEmpty = rs.getInt(0) == 0;
      rs.close();
      return bEmpty;
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
  }

  public boolean containsKey(Object arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean containsValue(Object arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public String get(Object sName) {
    try {
      this.statements.get.setString(1, (String) sName);
      ResultSet rs = this.statements.get.executeQuery();
      if( !rs.next() ) {
        return null;
      }
      String sRet = rs.getString(0);
      rs.close();
      return sRet;
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
  }

  /**
   * Nastaví proměnnou na danou hodnotu.
   * Pokud je hodnota null, hodnotu z tabulky vymaže.
   * 
   * @param sName   jméno nastavované proměnné.
   * @param sValue  nastavovaná hodnota proměnné.
   * @return        nastavovanou hodnotu.
   */
  public String put( String sName, String sValue ) {
    try {
      if( sValue == null ){
        this.delete( sName );
      }else{
        this.statements.get.setString(1, sName);
        this.statements.get.setString(2, sValue);
        this.statements.get.executeUpdate();
      }
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
    return sValue;
  }

  public String remove(Object sName) {
    String sValue = this.get(sName);
    try {
      this.delete((String) sName);
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
    return sValue;
  }
  
  public void delete( String sName ) throws SQLException {
    this.statements.delete.setString(1, sName);
    this.statements.get.executeUpdate();
  }

  public void putAll(Map arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void clear() {
    try {
      this.statements.clear.executeUpdate();
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
  }

  public Set<String> keySet() {
    try {
      Set<String> set = new HashSet<String>();
      ResultSet rs = this.statements.keySet.executeQuery();
      while( rs.next() ) {
        set.add(rs.getString(1));
      }
      rs.close();
      return set;
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
  }

  public Collection values() {
    try {
      Set<String> set = new HashSet<String>();
      ResultSet rs = this.statements.values.executeQuery();
      while( rs.next() ) {
        set.add(rs.getString(1));
      }
      rs.close();
      return set;
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
  }

  public Set entrySet() {
    try {
      Set set = new HashSet<JdbcMap.Entry>();
      ResultSet rs = this.statements.entrySet.executeQuery();
      while( rs.next() ) {
        JdbcMap.Entry entry = new JdbcMap.Entry( rs.getString(1), rs.getString(2) );
        set.add(entry);
      }
      rs.close();
      return set;
    } catch( SQLException ex ) {
      Logger.getLogger(JdbcMap.class.getName()).log(Level.SEVERE, null, ex);
      throw new NullPointerException();
    }
  }

  public String getSTable() {
    return sTable;
  }

  private void prepareStatements() throws SQLException {
    this.statements.clear = conn.prepareStatement("TRUNCATE "+this.sTable+"");
    this.statements.containsKey = conn.prepareStatement("SELECT COUNT(*) FROM "+this.sTable+" WHERE name=?");
    this.statements.containsValue = conn.prepareStatement("SELECT COUNT(*) FROM "+this.sTable+" WHERE value=?");
    this.statements.entrySet = conn.prepareStatement("SELECT name, value FROM "+this.sTable);
    this.statements.isEmpty = conn.prepareStatement("SELECT COUNT(*) FROM "+this.sTable);
    this.statements.keySet = conn.prepareStatement("SELECT name FROM "+this.sTable);
    this.statements.delete = conn.prepareStatement("DELETE FROM "+this.sTable+" WHERE name=?");
    this.statements.values = conn.prepareStatement("SELECT value FROM "+this.sTable);
    this.statements.put = conn.prepareStatement(
            "INSERT "+this.sTable+" SET name=?, value=? ON DUPLICATE KEY UPDATE `value`=VALUE(`value`)");
    this.statements.get = conn.prepareStatement("SELECT value FROM "+this.sTable+" WHERE name=?");
  }
  
  
  
  // ------ EntrySet ------- //
  public class Entry implements Map.Entry<String,String>{
    
    String key, value;

    public Entry(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return this.key;
    }

    public String getValue() {
      return this.value;
    }

    public String setValue(String val) {
      String sTmp = this.value;
      this.value = val;
      return sTmp;
    }
  
  }
  
  

}// class JdbcMap
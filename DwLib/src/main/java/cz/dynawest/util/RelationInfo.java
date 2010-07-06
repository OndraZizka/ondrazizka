
package cz.dynawest.util;

/**
 *
 * @author Ondřej Žižka
 */
public class RelationInfo {

  public enum Type{
    REL_1_1,
    REL_1_01,
    REL_1_0N,
    REL_1_1N,
  }

  Type type;
  String description = "";
  String parentTable;
  String parentId;
  String childTable;
  String childId;
  String childFK;

  public RelationInfo( Type type, String description, String parentTable, String parentId, String childTable, String childId, String childFK ) {
    this.type = type;
    this.description = description;
    this.parentTable = parentTable;
    this.parentId = parentId;
    this.childTable = childTable;
    this.childId = childId;
    this.childFK = childFK;
  }

  public RelationInfo( Type type, String parentTable, String parentId, String childTable, String childId, String childFK ) {
    this.type = type;
    this.parentTable = parentTable;
    this.parentId = parentId;
    this.childTable = childTable;
    this.childId = childId;
    this.childFK = childFK;
  }



  public String getChildFK() {    return childFK;  }
  public String getChildId() {    return childId;  }
  public String getChildTable() {    return childTable;  }
  public String getDescription() {    return description;  }
  public String getParentId() {    return parentId;  }
  public String getParentTable() {    return parentTable;  }
  public Type getType() {    return type;  }


}// class RelationInfo





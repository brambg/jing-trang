package com.thaiopensource.xml.dtd.om;

public class EnumGroup {
  private final EnumGroupMember[] members;

  public EnumGroup(EnumGroupMember[] members) {
    this.members = members;
  }

  public EnumGroupMember[] getMembers() {
    EnumGroupMember[] tem = new EnumGroupMember[members.length];
    System.arraycopy(members, 0, tem, 0, members.length);
    return tem;
  }
  
  public void accept(EnumGroupVisitor visitor) throws Exception {
    for (EnumGroupMember member : members) member.accept(visitor);
  }
}

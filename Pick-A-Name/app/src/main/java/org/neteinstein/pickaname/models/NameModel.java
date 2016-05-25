package org.neteinstein.pickaname.models;

public class NameModel
{
  private String gender;
  private String name;
  private String note;

  public String getGender()
  {
    return this.gender;
  }

  public String getName()
  {
    return this.name;
  }

  public String getNote()
  {
    return this.note;
  }

  public void setGender(String paramString)
  {
    this.gender = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setNote(String paramString)
  {
    this.note = paramString;
  }
}
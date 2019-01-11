package org.ditw.nameUtils.nameparser.v2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ditw.nameUtils.nameparser.v2.NameParserUtils.*;
/**
 * Created by dele on 2017-03-30.
 */
public class ParsedName implements Serializable {

  public final Map<String, String> sourceMap;
  public ParsedName(Map<String, String> inMap) {
    sourceMap = inMap;
  }

  public String getSource() {
    return concatStrings(sourceMap.values());
  }

  private String lastName = EmptyName;
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String v) {
    lastName = emptyNameIfNull(v);
  }
  private String lastName4Namespace = EmptyName;
  public String getLastName4Namespace() {
    return lastName4Namespace;
  }
  public void setLastName4Namespace(String v) {
    lastName4Namespace = v;
  }

  private String prefix = EmptyName;
  public String getPrefix() {
    return prefix;
  }
  public void setPrefix(String v) {
    prefix = emptyNameIfNull(v);
  }
  private String firstName = EmptyName;
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String v) {
    firstName = emptyNameIfNull(v);
  }

  private String middleName = EmptyName;
  public String getMiddleName() {
    return middleName;
  }
  public void setMiddleName(String v) {
    middleName = emptyNameIfNull(v);
  }

  private String initials = EmptyName;
  public String getInitials() {
    return initials;
  }
  public void setInitials(String v) {
    initials = emptyNameIfNull(v);
  }

  private List<String> nobles = EmptyNobles;
  public void updateNobles(List<String> n) {
    List<String> r = new ArrayList<>(nobles.size() + n.size());
    r.addAll(nobles);
    r.addAll(n);
    nobles = r;
  }
  public String getNobles() {
    return concatStrings(nobles);
  }

  private List<String> suffixes = EmptyNobles;
  public void updateSuffix(String suffix) {
    List<String> r = new ArrayList<>(suffixes.size() + 1);
    r.addAll(suffixes);
    r.add(suffix);
    suffixes = r;
  }
  public String getSuffixes() {
    return concatStrings(suffixes);
  }

  private String signature = EmptyName;
  public String getSignature() {
    return signature;
  }
  public void setSignature(String v) {
    signature = emptyNameIfNull(v);
  }


}

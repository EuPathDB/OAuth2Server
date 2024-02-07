package org.gusdb.oauth2.client.veupathdb;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gusdb.oauth2.shared.IdTokenFields;
import org.json.JSONObject;

/**
 * Represents a VEupathDB user
 *
 * @author rdoherty
 */
public class User {

  public static final Map<String,UserProperty> USER_PROPERTIES = createUserPropertyDefs();

  private static Map<String,UserProperty> createUserPropertyDefs() {
    List<UserProperty> userProps = List.of(
        new UserProperty("username", "Username", "username", false, false, false, User::getUsername, User::setUsername),
        new UserProperty("firstName", "First Name", "first_name", true, true, false, User::getFirstName, User::setFirstName),
        new UserProperty("middleName", "Middle Name", "middle_name", false, true, false, User::getMiddleName, User::setMiddleName),
        new UserProperty("lastName", "Last Name", "last_name", true, true, false, User::getLastName, User::setLastName),
        new UserProperty("organization", "Organization", "organization", true, true, false, User::getOrganization, User::setOrganization),
        new UserProperty("interests", "Interests", "interests", false, false, true, User::getInterests, User::setInterests)
    );
    return Collections.unmodifiableMap(userProps.stream().collect(Collectors.toMap(UserProperty::getName, x -> x)));
  }

  // immutable fields supplied by bearer token
  private final long _userId;
  private final boolean _isGuest;
  private final String _signature;
  private final String _stableId;

  // mutable fields that may need to be fetched
  private String _email; // standard; not a user property
  private String _username;
  private String _firstName;
  private String _middleName;
  private String _lastName;
  private String _organization;
  private String _interests;

  public User(long userId, boolean isGuest, String signature, String stableId) {
    _userId = userId;
    _isGuest = isGuest;
    _signature = signature;
    _stableId = stableId;
  }

  public User(JSONObject json) {
    this(
        Long.valueOf(json.getString(IdTokenFields.sub.name())),
        json.getBoolean(IdTokenFields.is_guest.name()),
        json.getString(IdTokenFields.signature.name()),
        json.getString(IdTokenFields.preferred_username.name())
    );
  }

  public void setPropertyValues(JSONObject json) {
    for (UserProperty userProp : User.USER_PROPERTIES.values()) {
      userProp.setValue(this, json.optString(userProp.getName(), null));
    }
  }

  public long getUserId() {
    return _userId;
  }

  public boolean isGuest() {
    return _isGuest;
  }

  public String getSignature() {
    return _signature;
  }

  public String getStableId() {
    return _stableId;
  }

  protected void fetchUserInfo() {
    // nothing to do in this base class; all info must be explicitly set
  }

  public String getEmail() {
    fetchUserInfo();
    return _email;
  }

  public User setEmail(String email) {
    _email = email;
    return this;
  }

  public String getUsername() {
    fetchUserInfo();
    return _username;
  }

  public User setUsername(String username) {
    _username = username;
    return this;
  }

  public String getFirstName() {
    fetchUserInfo();
    return _firstName;
  }

  public User setFirstName(String firstName) {
    _firstName = firstName;
    return this;
  }

  public String getMiddleName() {
    fetchUserInfo();
    return _middleName;
  }

  public User setMiddleName(String middleName) {
    _middleName = middleName;
    return this;
  }

  public String getLastName() {
    fetchUserInfo();
    return _lastName;
  }

  public User setLastName(String lastName) {
    _lastName = lastName;
    return this;
  }

  public String getOrganization() {
    fetchUserInfo();
    return _organization;
  }

  public User setOrganization(String organization) {
    _organization = organization;
    return this;
  }

  public String getInterests() {
    fetchUserInfo();
    return _interests;
  }

  public User setInterests(String interests) {
    _interests = interests;
    return this;
  }

  /**
   * Provides a "pretty" display name for this user
   * 
   * @return display name for this user
   */
  public String getDisplayName() {
    return isGuest() ? "Guest User" : (
        formatNamePart(getFirstName()) +
        formatNamePart(getMiddleName()) +
        formatNamePart(getLastName())).trim();
  }

  private static String formatNamePart(String namePart) {
    return (namePart == null || namePart.isEmpty() ? "" : " " + namePart.trim());
  }

  @Override
  public String toString() {
    return "User #" + getUserId() + " - " + getEmail();
  }

  @Override
  public int hashCode() {
    return String.valueOf(getUserId()).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof User)) {
      return false;
    }
    return getUserId() == ((User)obj).getUserId();
  }

}

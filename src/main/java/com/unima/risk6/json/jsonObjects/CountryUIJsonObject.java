package com.unima.risk6.json.jsonObjects;

import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;

/**
 * Wrapper class for parsing json file with all SVG paths of countries
 *
 * @author mmeider
 */

public class CountryUIJsonObject {

  private String path;

  private CountryName countryName;

  public CountryUIJsonObject(String path, CountryName countryName) {
    this.path = path;
    this.countryName = countryName;
  }

  public String getPath() {
    return path;
  }

  public CountryName getCountryName() {
    return countryName;
  }

  public void setCountryName(CountryName countryName) {
    this.countryName = countryName;
  }

  public void setPath(String path) {
    this.path = path;
  }


}

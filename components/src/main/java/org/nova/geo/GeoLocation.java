package org.nova.geo;

import java.time.ZoneId;

import org.nova.localization.CountryCode;

public record GeoLocation(LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId)
{
}


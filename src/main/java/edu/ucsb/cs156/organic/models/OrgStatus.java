package edu.ucsb.cs156.organic.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OrgStatus {
  private String org;
  private boolean githubAppInstalled;
  private String name;

  @Builder.Default
  private boolean exceptionThrown  = false;

  @Builder.Default
  private String exceptionMessage = "";
}

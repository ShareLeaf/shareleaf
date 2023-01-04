package co.shareleaf.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Biz Melesse
 * created on 11/16/22
 */
@Getter
@Setter
public class VideoInfoDto {
  private String url;
  private String imageUrl;
  private String caption;
  private String permalink;
  private String videoIdOverride;
}

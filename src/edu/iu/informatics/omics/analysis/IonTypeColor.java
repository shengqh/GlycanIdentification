/*
 * Created on 2005-11-14
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package edu.iu.informatics.omics.analysis;

import java.awt.Color;
import java.util.HashMap;

import cn.ac.rcpa.bio.proteomics.IonType;

public class IonTypeColor {
  private static HashMap<IonType, Color> colorMap = new HashMap<IonType, Color>();

  static {
    colorMap.put(IonType.Y, Color.RED);
    colorMap.put(IonType.B, Color.BLUE);
    colorMap.put(IonType.Y2, Color.MAGENTA);
    colorMap.put(IonType.B2, Color.CYAN);
    colorMap.put(IonType.NEUTRAL_LOSS_PHOSPHO, Color.GREEN);
    colorMap.put(IonType.NEUTRAL_LOSS, Color.GREEN);
    colorMap.put(IonType.PRECURSOR, Color.ORANGE);
    colorMap.put(IonType.PRECURSOR_NEUTRAL_LOSS_PHOSPHO, Color.ORANGE);
    colorMap.put(IonType.PRECURSOR_NEUTRAL_LOSS, Color.ORANGE);
  }

  public static Color getColor(IonType ionType) {
    if (colorMap.containsKey(ionType)) {
      return colorMap.get(ionType);
    }

    throw new IllegalStateException("Undefine color for " + ionType);
  }

  public static String colorToHtmlColor(Color color) {
    final String red = getColorComponentHexString(color.getRed());
    final String green = getColorComponentHexString(color.getGreen());
    final String blue = getColorComponentHexString(color.getBlue());
    return '#' + red + green + blue;
  }

  public static String getHtmlColor(IonType ionType) {
    Color color = colorMap.get(ionType);
    if (null == color) {
      return "#000000";
    }

    return colorToHtmlColor(color);
  }

  private static String getColorComponentHexString(int value) {
    String result = Integer.toHexString(value);
    if (1 == result.length()) {
      result = '0' + result;
    }

    return result.toUpperCase();
  }
}

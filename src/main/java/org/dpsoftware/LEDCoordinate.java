/*
  LEDCoordinate.java

  Firefly Luciferin, very fast Java Screen Capture software designed
  for Glow Worm Luciferin firmware.

  Copyright (C) 2020 - 2022  Davide Perini

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.dpsoftware;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dpsoftware.config.Constants;

import java.util.LinkedHashMap;


/**
 * X Y coordinate for LEDs
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LEDCoordinate {

    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * Init FullScreen LED Matrix with a default general purpose config
     *
     * @return LED Matrix
     */
    public LinkedHashMap<Integer, LEDCoordinate> initFullScreenLedMatrix(int screenWidth, int screenHeight, int bottomRightLed, int rightLed, int topLed, int leftLed,
                                                                         int bottomLeftLed, int bottomRowLed, boolean splitBottomRow) {
        LinkedHashMap<Integer, LEDCoordinate> defaultLedMatrix = new LinkedHashMap<>();
        initializeLedMatrix(defaultLedMatrix, screenWidth, screenHeight, bottomRightLed, rightLed, topLed, leftLed, bottomLeftLed, bottomRowLed, splitBottomRow, Constants.AspectRatio.FULLSCREEN);
        return defaultLedMatrix;
    }

    /**
     * Init Letterbox LED Matrix with a default general purpose config
     *
     * @return LED letterbox matrix
     */
    public LinkedHashMap<Integer, LEDCoordinate> initLetterboxLedMatrix(int screenWidth, int screenHeight, int bottomRightLed, int rightLed, int topLed,
                                                                        int leftLed, int bottomLeftLed, int bottomRowLed, boolean splitBottomRow) {
        LinkedHashMap<Integer, LEDCoordinate> defaultLedMatrix = new LinkedHashMap<>();
        initializeLedMatrix(defaultLedMatrix, screenWidth, screenHeight, bottomRightLed, rightLed, topLed, leftLed, bottomLeftLed, bottomRowLed, splitBottomRow, Constants.AspectRatio.LETTERBOX);
        return defaultLedMatrix;
    }

    /**
     * Init Pillarbox LED Matrix with a default general purpose config
     *
     * @return LED letterbox matrix
     */
    public LinkedHashMap<Integer, LEDCoordinate> initPillarboxMatrix(int screenWidth, int screenHeight, int bottomRightLed, int rightLed,
                                                                     int topLed, int leftLed, int bottomLeftLed, int bottomRowLed, boolean splitBottomRow) {
        LinkedHashMap<Integer, LEDCoordinate> defaultLedMatrix = new LinkedHashMap<>();
        initializeLedMatrix(defaultLedMatrix, screenWidth, screenHeight, bottomRightLed, rightLed, topLed,
                leftLed, bottomLeftLed, bottomRowLed, splitBottomRow, Constants.AspectRatio.PILLARBOX);
        return defaultLedMatrix;
    }

    /**
     * Calculate borders for fit to screen, 4:3, 16:9, 21:9, 32:9
     *
     * @param screenWidth  screen width
     * @param screenHeight screen height
     */
    public static int calculateBorders(int screenWidth, int screenHeight) {
        double aspectRatio = Math.round(((double) screenWidth / (double) screenHeight) * 10) / 10.00; // Round aspect ratio to 2 decimals
        if (aspectRatio >= 1.2 && aspectRatio <= 1.4) { // standard 4:3
            return 0;
        } else if (aspectRatio >= 1.6 && aspectRatio <= 1.8) { // widescreen 16:9
            return ((screenWidth * 480) / 3840) + 100;
        } else if (aspectRatio >= 2.1 && aspectRatio <= 2.5) {
            return ((screenWidth * 440) / 3440) + 100; // ultra wide screen 21:9
        } else if (aspectRatio > 2.5 && aspectRatio <= 3.7) {
            return ((screenWidth * 960) / 3840) + 100; // ultra wide screen 32:9
        } else {
            return 0;
        }
    }

    void initializeLedMatrix(LinkedHashMap<Integer, LEDCoordinate> defaultLedMatrix, int width, int height, int bottomRightLed, int rightLed,
                             int topLed, int leftLed, int bottomLeftLed, int bottomRowLed, boolean splitBottomRow, Constants.AspectRatio aspectRatio) {
        var ledNum = 0;
        int letterboxBorder = 0, pillarboxBorder = 0;
        if (aspectRatio == Constants.AspectRatio.LETTERBOX) {
            letterboxBorder = height / Constants.LETTERBOX_RATIO;
        } else if (aspectRatio == Constants.AspectRatio.PILLARBOX) {
            pillarboxBorder = calculateBorders(width, height);
        }
        width = width - (pillarboxBorder * 2);
        height = height - (letterboxBorder * 2);
        int topBottomAreaHeight = height / Constants.TOP_BOTTOM_AREA_HEIGHT;
        int sideAreaWidth = width / Constants.SIDE_AREA_WIDTH;
        // TODO
        var splitBottomMargin = (width * 15) / 100;
        if (splitBottomRow) {
            // bottomRight LED strip
            if (bottomRightLed > 0) {
                var bottomLedDistance = (((width - splitBottomMargin) / 2)) / bottomRightLed;
                for (int i = 1; i <= bottomRightLed; i++) {
                    ledNum++;
                    int x = ((bottomLedDistance * i) - bottomLedDistance) + ((width / 2) + (splitBottomMargin / 2));
                    defaultLedMatrix.put(ledNum, new LEDCoordinate(x + pillarboxBorder, (height - (topBottomAreaHeight)) + letterboxBorder,
                            (width - splitBottomMargin) / (bottomLeftLed + bottomRightLed), topBottomAreaHeight));
                }
            }
        } else {
            // bottomLeft LED strip
            if (bottomRowLed > 0) {
                var bottomLedLeftDistance = width / bottomRowLed;
                for (int i = 1; i <= bottomRowLed; i++) {
                    ledNum++;
                    int x = ((bottomLedLeftDistance * i) - bottomLedLeftDistance);
                    defaultLedMatrix.put(ledNum, new LEDCoordinate(x + pillarboxBorder, (height - topBottomAreaHeight) + letterboxBorder,
                            (width) / (bottomLeftLed + bottomRightLed), topBottomAreaHeight));
                }
            }
        }
        // right LED strip
        if (rightLed > 0) {
            var rightLedDistance = (height - (topBottomAreaHeight * 2)) / rightLed;
            for (int i = 1; i <= rightLed; i++) {
                ledNum++;
                defaultLedMatrix.put(ledNum, new LEDCoordinate((width - sideAreaWidth) + pillarboxBorder, ((height - (rightLedDistance * i) - topBottomAreaHeight)) + letterboxBorder,
                        sideAreaWidth, (height - (topBottomAreaHeight * 2)) / rightLed));
            }
        }
        // top LED strip
        if (topLed > 0) {
            var topLedDistance = width / topLed;
            for (int i = 1; i <= topLed; i++) {
                ledNum++;
                int x = (width - (topLedDistance * i));
                //noinspection SuspiciousNameCombination
                defaultLedMatrix.put(ledNum, new LEDCoordinate(x + pillarboxBorder, letterboxBorder, topLedDistance, topBottomAreaHeight));
            }
        }
        // left LED strip
        if (leftLed > 0) {
            var leftLedDistance = (height - (topBottomAreaHeight * 2)) / leftLed;
            for (int i = leftLed; i >= 1; i--) {
                ledNum++;
                defaultLedMatrix.put(ledNum, new LEDCoordinate(pillarboxBorder, ((height - (leftLedDistance * i)) - topBottomAreaHeight) + letterboxBorder,
                        sideAreaWidth, (height - (topBottomAreaHeight * 2)) / leftLed));
            }
        }
        if (splitBottomRow) {
            // bottomLeft LED strip
            if (bottomLeftLed > 0) {
                var bottomLedLeftDistance = (((width - splitBottomMargin) / 2)) / bottomLeftLed;
                for (int i = 1; i <= bottomLeftLed; i++) {
                    ledNum++;
                    int x = (bottomLedLeftDistance * i) - bottomLedLeftDistance;
                    defaultLedMatrix.put(ledNum, new LEDCoordinate(x + pillarboxBorder, (height - topBottomAreaHeight) + letterboxBorder, bottomLedLeftDistance, topBottomAreaHeight));
                }
            }
        }
    }
}
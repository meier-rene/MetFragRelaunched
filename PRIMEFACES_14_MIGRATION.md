# PrimeFaces 14 Migration Guide

## Overview

This document describes the migration from PrimeFaces 12.0.0 to PrimeFaces 14.0.0 in the MetFragRelaunched application. The most significant change is the transition from jqPlot-based charts to Chart.js-based charts.

## Changes Made

### 1. Dependency Update

**File:** `pom.xml`

Updated PrimeFaces version from 12.0.0 to 14.0.0:
```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>14.0.0</version>
    <classifier>jakarta</classifier>
</dependency>
```

### 2. Watermark Component Migration

The `<p:watermark>` component was deprecated in PrimeFaces 13 and removed in PrimeFaces 14. It has been replaced with HTML5 `placeholder` attribute via JSF passthrough.

**Old (PrimeFaces 12):**
```xml
<p:inputText id="myInput" value="#{bean.value}" />
<p:watermark for="myInput" value="Enter text here" id="myWatermark" />
```

**New (PrimeFaces 14):**
```xml
<p:inputText id="myInput" value="#{bean.value}" pt:placeholder="Enter text here" />
```

**Required namespace:**
```xml
xmlns:pt="http://xmlns.jcp.org/jsf/passthrough"
```

**Migrated files:**
- `comparespectraD3JSInclude.xhtml` - 2 SMILES input fields
- `candidateScoresPrime.xhtml` - 2 SMARTS input fields
- `candidateFiltersPrime.xhtml` - 2 SMARTS input fields

### 3. Java Model Classes

#### CandidateStatistics.java
- **Old:** Used `org.primefaces.model.chart.LineChartModel` and `LineChartSeries` (jqPlot)
- **New:** Uses `org.primefaces.model.charts.line.LineChartModel` and `LineChartDataSet` (Chart.js)
- **Changes:**
  - Replaced `LineChartSeries` with `LineChartDataSet`
  - Data points are now added as List<Object> instead of using `series.set(x, y)`
  - Chart configuration moved from jqPlot-specific methods to Chart.js options
  - Axis configuration simplified (details handled in JavaScript extender)

#### Molecule.java
- **Old:** Used `org.primefaces.model.chart.HorizontalBarChartModel` (jqPlot)
- **New:** Uses `org.primefaces.model.charts.bar.BarChartModel` with `indexAxis: 'y'` (Chart.js)
- **Changes:**
  - Replaced `HorizontalBarChartModel` with `BarChartModel`
  - Set `options.setIndexAxis("y")` to make bar chart horizontal
  - Replaced `ChartSeries` with `BarChartDataSet`
  - Data and labels are now separate collections
  - Chart configuration simplified

#### BeanSettingsContainer.java
- **Changes:**
  - Updated spectrum model generation to use Chart.js LineChartModel
  - Data structure changed from series-based to dataset-based
  - Each peak is now a separate dataset with two data points (for vertical lines)

#### MetFragWebBean.java
- **Changes:**
  - Updated fragments model generation to use Chart.js LineChartModel
  - Peak colors (matched/non-matched/unused) now set via `borderColor` and `backgroundColor`
  - Removed jqPlot-specific methods like `setSeriesColors()`

### 4. XHTML View Files

#### statistics.xhtml
- **Changes:**
  - Updated `extenderScore()` JavaScript function to use Chart.js API
  - Old jqPlot configuration (`this.cfg.cursor`, `this.cfg.axes`) replaced with Chart.js options
  - Zoom functionality now uses Chart.js zoom plugin (if available)
  - Tooltips configured via `plugins.tooltip.callbacks`

#### candiateScoreDistribution.xhtml
- **Changes:**
  - Similar extender function updates as statistics.xhtml
  - Removed jqPlot-specific script includes
  - Updated to use Chart.js configuration structure

#### resultTable.xhtml
- **Changes:**
  - Updated bar chart extender to use Chart.js API
  - Scale configuration updated to use `options.scales.x` and `options.scales.y`
  - Removed jqPlot script includes
  - Disabled animations for better table performance

#### fragmentsViewDialog.xhtml
- **Changes:**
  - Added `extender="fragmentsViewExtender"` attribute to p:chart
  - Updated extender function to use Chart.js configuration
  - Tooltip format updated to Chart.js callback structure
  - Zoom configuration updated for Chart.js zoom plugin

## Key Differences Between jqPlot and Chart.js

### Data Structure
- **jqPlot:** Used `series.set(x, y)` to add data points
- **Chart.js:** Uses `List<Object>` with data points, separate labels array

### Configuration
- **jqPlot:** Configuration via Java methods like `setShowDatatip()`, `setMouseoverHighlight()`
- **Chart.js:** Configuration primarily via JavaScript extender functions using `options` object

### Axis Configuration
- **jqPlot:** `getAxis(AxisType.X).setLabel()`, `setTickInterval()`, etc.
- **Chart.js:** `options.scales.x.title.text`, configured in extender function

### Colors
- **jqPlot:** `setSeriesColors("color1,color2,...")`
- **Chart.js:** Individual dataset `borderColor` and `backgroundColor` properties

### Horizontal Bar Charts
- **jqPlot:** Dedicated `HorizontalBarChartModel` class
- **Chart.js:** Regular `BarChartModel` with `indexAxis: 'y'` option

## Testing Notes

Unit tests have been created in:
- `CandidateStatisticsTest.java` - Tests score distribution model generation
- `MoleculeTest.java` - Tests horizontal bar chart model generation

These tests verify:
- Model creation for various data scenarios
- Data structure integrity
- Property getters and setters
- Score calculation behavior

## Browser Compatibility

Chart.js provides better browser compatibility and modern features compared to jqPlot:
- Better responsive design support
- Touch/mobile interaction support
- Active development and maintenance
- Better animation capabilities
- Modern JavaScript (ES6+) support

## Zoom Functionality

The zoom functionality previously provided by jqPlot cursor plugin is now available through the Chart.js zoom plugin. The extender functions check for the plugin's availability before configuring zoom:

```javascript
if (typeof Chart.Zoom !== 'undefined') {
    this.cfg.options.plugins.zoom = {
        // zoom configuration
    };
}
```

## Known Limitations

1. Some advanced jqPlot-specific features may not have direct Chart.js equivalents
2. Chart aesthetics may differ slightly due to different rendering engines
3. Min/Max/StepSize tick configuration is handled primarily in JavaScript extenders rather than Java models

## Migration Checklist for Future Updates

- [ ] Verify all chart rendering in the UI
- [ ] Test zoom functionality in all charts
- [ ] Validate tooltip behavior
- [ ] Check chart export functionality
- [ ] Test chart interactions (click, hover, select)
- [ ] Verify responsive behavior on different screen sizes
- [ ] Test with different data sizes and edge cases

## Resources

- [PrimeFaces 14 Documentation](https://primefaces.github.io/primefaces/14_0_0/)
- [PrimeFaces 14 Chart Documentation](https://github.com/primefaces/primefaces/blob/master/docs/14_0_0/components/chart.md)
- [Chart.js Documentation](https://www.chartjs.org/docs/latest/)
- [PrimeFaces Migration Guide](https://github.com/primefaces/primefaces/tree/master/docs/migrationguide)

/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
// @flow
import React from 'react';
import classNames from 'classnames';
import BubblePopup from '../../../components/common/BubblePopup';
import FormattedDate from '../../../components/ui/FormattedDate';
import ChartLegendIcon from '../../../components/icons-components/ChartLegendIcon';
import type { Serie } from '../../../components/charts/AdvancedTimeline';

type Props = {
  formatValue: (number | string) => string,
  graph: string,
  graphWidth: number,
  selectedDate: Date,
  series: Array<Serie & { translatedName: string }>,
  tooltipIdx: number,
  tooltipPos: number
};

const TOOLTIP_WIDTH = 50;

export default class PreviewGraphTooltips extends React.PureComponent {
  props: Props;

  render() {
    const { tooltipIdx } = this.props;
    const top = 10;
    let left = this.props.tooltipPos;
    let customClass;
    if (left > this.props.graphWidth - TOOLTIP_WIDTH - 20) {
      left -= TOOLTIP_WIDTH;
      customClass = 'bubble-popup-right';
    }
    return (
      <BubblePopup customClass={customClass} position={{ top, left, width: TOOLTIP_WIDTH }}>
        <div className="project-activity-graph-tooltip">
          <div className="project-activity-graph-tooltip-title spacer-bottom">
            <FormattedDate date={this.props.selectedDate} format="LL" />
          </div>
          <table className="width-100">
            <tbody>
              {this.props.series.map(serie => {
                const point = serie.data[tooltipIdx];
                if (!point || (!point.y && point.y !== 0)) {
                  return null;
                }
                return (
                  <tr key={serie.name} className="overview-analysis-graph-tooltip-line">
                    <td className="thin">
                      <ChartLegendIcon
                        className={classNames(
                          'spacer-right line-chart-legend',
                          'line-chart-legend-' + serie.style
                        )}
                      />
                    </td>
                    <td
                      className={classNames(
                        'overview-analysis-graph-tooltip-value',
                        'text-right spacer-right thin'
                      )}>
                      {this.props.formatValue(point.y)}
                    </td>
                    <td>{serie.translatedName}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </BubblePopup>
    );
  }
}

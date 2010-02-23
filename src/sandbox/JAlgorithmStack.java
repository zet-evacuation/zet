/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * JAlgorithmStack.java
 *
 */
package sandbox;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Stack;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 *
 */
public class JAlgorithmStack extends JTable {
/*    
    private static final Localization LOCALIZATION = Localization.getInstance();
    private static final String ALGORITHM_KEY = "Algorithmus";
    private static final String PROGRESS_KEY = "Fortschritt";
    private static final String ALGORITHM_PROGRESS_TEXT_KEY = "%1$d%% [Laufzeit: %2$s - Verbleibend: %3$s (geschätzt)]";

    protected AlgorithmModel model;
    protected TableCellRenderer renderer;

    public JAlgorithmStack() {
        super();
        model = new AlgorithmModel();
        setFocusable(false);
        setModel(model);
        renderer = new ProgressRenderer();
    }
    
    private String getLocalizedString(String key) {
        return key;
        //return LOCALIZATION.getString(key);
    }
    
    private String getFormattedTimeString(long timeInNanoseconds) {
        long time = timeInNanoseconds;
        if (time == 0) {
            return "0 ns";
        } else {
            int level = (int) Math.ceil(Math.log(time + 1) / Math.log(1000));
            switch (level) {
                case 1:
                    return time + " ns";
                case 2:
                    return String.format("%1$d.%2$03d µs", time / 1000, time % 1000);
                case 3:
                    return String.format("%1$d.%2$03d ms", time / 1000000, (time / 1000) % 1000);
                default:
                    long ms = (time / 1000000) % 1000;
                    long s = time / 1000000000;
                    long min = s / 60;
                    long h = min / 60;
                    if ( s < 60) {
                        return String.format("%1$d.%2$03d s", s, ms);
                    } else if (s >= 60 && s < 3600) {
                        return String.format("%1$d:%2$02d min", min, s);
                    } else {
                        return String.format("%1$d:%2$02d h", h, min);
                    }
            }
        }       
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 0) {
            return super.getCellRenderer(row, column);
        } else if (column == 1) {
            return renderer;
        } else {
            throw new AssertionError("This should not happen.");
        }
    }
    /*
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(300, 50);
    }
    */
    /*
    public void handleEvent(Event event) {
        if (event instanceof OperationExecutingEvent) {
            int count = 0;
            for (int row = 0; row < riddle.rows(); row++) {
                for (int column = 0; column < riddle.columns(); column++) {
                    for (int digit = 0; digit < 10; digit++) {
                        if (riddle.getMatrix().get(row, column).getDigitStatus()[digit] != DigitStatus.CURRENT) {
                            count++;
                        }
                    }
                }
            }
            if (model.getStack().size() == 0) {
                model.getTotal().setNumberOfSteps(count);
            } else {
                model.getStack().peek().setNumberOfSteps(count);
            }
            model.fireTableDataChanged();
        }
        if (event instanceof RecursionDepthDecreasedEvent) {
            model.removeRow();
        }
        if (event instanceof RecursionDepthIncreasedEvent) {
            RecursionDepthIncreasedEvent e = (RecursionDepthIncreasedEvent) event;
            model.addRow(e.getVariable(), e.getNumber(), e.getAlreadyDone(), e.getMaximumNumberOfSteps());
        }
        if (event instanceof RiddleChangedEvent) {
            RiddleChangedEvent e = (RiddleChangedEvent) event;
            riddle = e.getRiddle();
            model.clear();
            model.getTotal().setMaximumNumberOfSteps(9 * riddle.rows() * riddle.columns());
            model.fireTableDataChanged();
        }
        if (event instanceof SolverStartedEvent) {
            model.clear();
            model.getTotal().setMaximumNumberOfSteps(9 * riddle.rows() * riddle.columns());
            model.fireTableDataChanged();
        }
        if (event instanceof SolverTerminatedEvent) {
            SolverTerminatedEvent e = (SolverTerminatedEvent) event;
            switch (e.getReason()) {
                case DONE:
                    model.getStack().clear();
                    model.getTotal().setNumberOfSteps(model.getTotal().getMaximumNumberOfSteps());
                    model.fireTableDataChanged();
                    break;
                case EXCEPTION:
                    break;
                case NO_OPERATIONS:
                    break;
                case STOPPED:
                    break;
                default:
                    throw new AssertionError("This should not happen.");
            }
        }
    }*/
/*
    protected class AlgorithmModel extends AbstractTableModel {

        protected Stack<AlgorithmInformation> stack;

        public AlgorithmModel() {
            stack = new Stack<AlgorithmInformation>();
        }

        public void addAlgorithm(Algorithm algorithm) {
            AlgorithmInformation info = new AlgorithmInformation(algorithm);
            stack.push(info);
            model.fireTableRowsInserted(stack.size() - 1, stack.size() - 1);
        }

        public void finishAlgorithm() {
            stack.pop();
            model.fireTableRowsDeleted(stack.size(), stack.size());
        }

        public void reset() {
            stack.clear();
            model.fireTableDataChanged();
        }

        public int getRowCount() {
            return stack.size();
        }

        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return getLocalizedString(ALGORITHM_KEY);
                case 1:
                    return getLocalizedString(PROGRESS_KEY);
                default:
                    throw new AssertionError("This should not happen.");
            }
        }

        public Stack<AlgorithmInformation> getStack() {
            return stack;
        }

        public synchronized Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    if (rowIndex == 0) {
                        return "Gesamtfortschritt:";
                    } else {
                        synchronized (this) {
                            if (rowIndex - 1 < stack.size()) {
                                return String.format("Teste %1$s = %2$s", stack.get(rowIndex - 1).getVariable().toText(), stack.get(rowIndex - 1).getValue());
                            } else {
                                return "";
                            }
                        }
                    }
                case 1:
                    if (rowIndex == 0) {
                        return total;
                    } else {
                        synchronized (this) {
                            if (rowIndex - 1 < stack.size()) {
                                return stack.get(rowIndex - 1);
                            } else {
                                return null;
                            }
                        }
                    }
                default:
                    throw new AssertionError("This should not happen.");
            }
        }
    }

    protected class ProgressRenderer extends JProgressBar implements TableCellRenderer {

        public ProgressRenderer() {
            super();
            setMaximum(100);
            setString(null);
            setStringPainted(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null || !(value instanceof AlgorithmInformation)) {
                setString(null);
                setValue(0);
                return this;
            }            
            AlgorithmInformation info = (AlgorithmInformation) value;
            setString(String.format(getLocalizedString(ALGORITHM_PROGRESS_TEXT_KEY), info.getProgress(), getFormattedTimeString(0), getFormattedTimeString(0)));
            setValue(info.getProgress());
            return this;
        }
    }

    private class AlgorithmInformation {

        private Algorithm algorithm;
        private int progress;
        private final long startTime = 00;

        private AlgorithmInformation(Algorithm algorithm) {
            this.algorithm = algorithm;
            startTime = algorithm.getStartTime();
        }

        private Algorithm getAlgorithm() {
            return algorithm;
        }

        private int getProgress() {
            return progress;
        }

        private void setProgress(int progress) {
            this.progress = progress;
        }        
        
        private long getStartTime() {
            return startTime;
        }
    }
    
    public static void main(String[] args) {
        long time = 234087054;
        System.out.println(String.format("%1$d.%2$03d ms", time / 1000000, (time / 1000) % 1000));
        System.out.println(Math.ceil(Math.log(time + 1) / Math.log(1000)));
    }
*/
}

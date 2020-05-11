/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package statistic.ca.gui;

import io.visualization.EvacuationSimulationResults;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.UUID;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import batch.BatchResult;
import batch.BatchResultEntry;
import org.zetool.common.datastructure.NamedIndex;
import ds.PropertyContainer;
import org.zet.cellularautomaton.statistic.MultipleCycleCAStatistic;
import org.zet.cellularautomaton.statistic.exception.GroupOfIndsNoPotentialException;
import org.zet.cellularautomaton.statistic.exception.OneIndNoPotentialException;
import ds.GraphVisualizationResults;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.DeathCause;
import gui.ZETLoader;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import org.zet.cellularautomaton.statistic.exception.AllCyclesNoValueBecauseAlreadySafeException;
import org.zet.cellularautomaton.statistic.exception.AllCyclesNoValueBecauseNotSafeException;

/**
 *
 * @author Matthias Woste
 * @author Timon Kelter
 */
public class JCAStatisticPanel extends JPanel {

    private JSplitPane pane;
    private JPanel west;
    private JPanel east;
    private JComboBox statisticSource;
    private JComboBox statisticType;
    // private JPanel diagrams;
    private DiagramContainer diagrams;
    private JPanel properties;
    private JScrollPane contentD;
    private JScrollPane contentP;
    private JLabel timeInterval;
    private JTextField timeIntervalFrom;
    private JTextField timeIntervalTo;
    private JList assignmentList;
    private DefaultListModel assignmentListModel;
    private JList diagramCategoryList;
    private DefaultListModel diagramCategoryListModel;
    private JTable basicInformationTable;
    private JScrollPane basicInformationScrollPane;
    private static JFreeChart ausgangsverteilung;
    private static JFreeChart evakuierungsdauer;
    private static JFreeChart aveblockadezeit;
    private static JFreeChart maximaleGeschwindigkeitueberZeit;
    private static JFreeChart zurueckgelegteDistanz;
    private static JFreeChart minimaleDistanzzuminitialenAusgang;
    private static JFreeChart minimaleDistanzzumnaechstenAusgang;
    private static JFreeChart maximaleGeschwindigkeit;
    private static JFreeChart distanzueberZeit;
    private static JFreeChart durschnittlicheGeschwindigkeitueberZeit;
    private static JFreeChart durchschnittlicheGeschwindigkeit;
    private static JFreeChart panik;
    private static JFreeChart erschoepfung;
    private static JFreeChart ankunftskurve;
    private static JFreeChart maxblockadezeit;
    private static JFreeChart minblockadezeit;
    private static JFreeChart maxZeitBisSafe;
    private static JFreeChart aveZeitBisSafe;
    private static JFreeChart minZeitBisSafe;
    private static JFreeChart evakuierteIndividueninProzent;
    private JPanel diagramToRemove;
    private BatchResultEntryComboBoxModel model;
    private HashMap<String, ArrayList<UUID>> assignmentTypeToUUID;
    private ArrayList<Integer> assignmentIndexToShow;
    private ArrayList<Integer> diagramCategoryIndexToShow;
    private ArrayList<AssignmentGroupItem> assignmentGroups;
    private XYSeries dataset;
    private XYSeriesCollection datasetCollection;
    private ArrayList<Double> categoryDatasetValues;
    private ArrayList<String> categoryDatasetAssignments;
    private ChartData chartData;
    private EvacuationSimulationResults cavr;
    private GraphVisualizationResults gvr;
    private MultipleCycleCAStatistic mccas;
    private BatchResultEntry selectedBatchResultEntry;
    private EvacuationCellularAutomaton ca;
    private Double currentAverageStepsPerSeconds;
    /**
     * saves a list of lists, where each list consists of those individuals connected to the chosen assignment-type in
     * each batch-result-entry
     */
    private HashMap<UUID, ArrayList<Individual>> UUIDToIndividualsForEachResultEntry;
    private BatchResult result;
    private boolean noIndividualsInAtLeastOneAssignmentIndex = false;

    public JCAStatisticPanel() {
        super();
        assignmentTypeToUUID = new HashMap<>();
        assignmentIndexToShow = new ArrayList<>();
        diagramCategoryIndexToShow = new ArrayList<>();
        assignmentGroups = new ArrayList<>();
        UUIDToIndividualsForEachResultEntry = new HashMap<>();
        setLayout(new BorderLayout());
        addComponents();
    }

    public void setCellularAutomaton(EvacuationCellularAutomaton ca) {
        this.ca = ca;
    }

    public void setMultipleCycleCAStatistic(MultipleCycleCAStatistic mccas) {
        this.mccas = mccas;
    }

    public void setCA(EvacuationSimulationResults cavr) {
        this.cavr = cavr;
    }

    public void setGraph(GraphVisualizationResults gvr) {
        this.gvr = gvr;
    }

    public void setResult(BatchResult result) {
        this.result = result;

        diagramCategoryListModel.clear();
        diagramCategoryIndexToShow.clear();
        assignmentIndexToShow.clear();
        assignmentList.clearSelection();
        diagrams.removeAll();

        model.rebuild(result);
    }

    public JSplitPane getSplitPane() {
        return pane;
    }

    public void fillAssignmentTypeList(Map<String, UUID> mapping) {
        assignmentListModel.clear();
        assignmentTypeToUUID.clear();
        assignmentGroups.clear();
        assignmentIndexToShow.clear();
        for (String s : mapping.keySet()) {
            assignmentListModel.addElement(s);
            assignmentGroups.add(new AssignmentGroupItem(s, mapping.get(s)));
        }
        // For convenience pre-select the first entry
        if (assignmentListModel.size() > 0) {
            assignmentList.setSelectedIndex(0);
        }
    }

    /**
     * Wird aufgerufen, falls die in der ComboBox ein neuer Diagrammtyp ausgewählt wurde.
     *
     * @author Matthias Woste
     *
     */
    private class TypePerformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            categoryDatasetValues = new ArrayList<>();
            categoryDatasetAssignments = new ArrayList<>();
            datasetCollection = new XYSeriesCollection();
        }
    }

    /**
     * This class serves as a model for the JComboBox that contains the BatchResultEntries.
     */
    private class BatchResultEntryComboBoxModel extends DefaultComboBoxModel {

        BatchResult result;

        public void rebuild(BatchResult result) {
            this.result = result;

            removeAllElements();
            int index = 0;
            for (String e : result.getEntryNames()) {
                if (result.entryHasCa(index)) {
                    super.addElement(new NamedIndex(e, index));
                }
                index++;
            }
        }

        @Override
        public void setSelectedItem(Object object) {
            super.setSelectedItem(object);

            selectedBatchResultEntry = (BatchResultEntry) getSelectedItem();
            if (selectedBatchResultEntry.getCa()[0] != null) {
                //fillAssignmentTypeList(selectedBatchResultEntry.getCa()[0].getAssignmentTypes());
                setMultipleCycleCAStatistic(selectedBatchResultEntry.getMultipleCycleCAStatistics());
                setCellularAutomaton(selectedBatchResultEntry.getCa()[0]);
                currentAverageStepsPerSeconds = selectedBatchResultEntry.getAverageCAStepsPerSecond();
            } else {
                ZETLoader.sendMessage("This entry bears no cellular automaton results!");
            }
        }

        @Override
        public Object getSelectedItem() {
            try {
                if (result != null && super.getSelectedItem() != null) {
                    return result.getResult(((NamedIndex) super.getSelectedItem()).getIndex());
                } else {
                    return null;
                }
            } catch (IOException ex) {
                ZETLoader.sendError("Error while loading temp file: " + ex.getLocalizedMessage());
                return null;
            }
        }
    }

    private class AddToDiagramPerformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (assignmentIndexToShow.isEmpty()) {
                ZETLoader.sendError("Bitte wählen Sie zuerst eine Belegung aus!");
                return;
            }

            for (Integer i : assignmentIndexToShow) {
                diagramCategoryListModel.addElement(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? "" : " - " + assignmentGroups.get(i).toString()));
            }
            calculateStatistic(statisticType.getSelectedItem().toString());
        }
    }

    private class RemovePerformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            diagrams.remove(diagramToRemove);
        }
    }

    private class GroupPreformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (assignmentIndexToShow.size() > 1) {
                AssignmentGroupItem newGroup = new AssignmentGroupItem();
                for (Integer i : assignmentIndexToShow) {
                    newGroup.addItem(assignmentGroups.get(i).getAssignmentTypes(), assignmentGroups.get(i).getAssignmentUUIDs());
                }
                for (int i = assignmentIndexToShow.size() - 1; i >= 0; i--) {
                    assignmentGroups.remove(assignmentGroups.get(assignmentIndexToShow.get(i)));
                }
                assignmentGroups.add(newGroup);
                updateAssignmentList();
            }
        }
    }

    private class UngroupPreformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Integer i : assignmentIndexToShow) {
                if (assignmentGroups.get(i).getAssignmentTypes().size() > 1) {
                    for (String s : assignmentGroups.get(i).getAssignmentTypes()) {
                        AssignmentGroupItem newOne = new AssignmentGroupItem(s, assignmentGroups.get(i).getAssignmentUUIDs().get(assignmentGroups.get(i).getAssignmentTypes().indexOf(s)));
                        assignmentGroups.add(newOne);
                    }
                    assignmentGroups.remove(assignmentGroups.get(i));
                }
            }
            updateAssignmentList();
        }
    }

    private class CreateDiagramPerformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            showDiagramm(statisticType.getSelectedItem().toString());
            diagramCategoryListModel.clear();
            diagramCategoryIndexToShow.clear();
            assignmentIndexToShow.clear();
            assignmentList.clearSelection();
        }
    }

    private class RemoveFromDiagramPerformed implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = diagramCategoryIndexToShow.size() - 1; i >= 0; i--) {
                diagramCategoryListModel.remove(i);
                if (categoryDatasetAssignments != null && categoryDatasetAssignments.size() > i) {
                    categoryDatasetAssignments.remove(i);
                    categoryDatasetValues.remove(i);
                } else {
                    datasetCollection.removeSeries(i);
                }
            }
        }
    }

    private void updateAssignmentList() {
        assignmentListModel.clear();
        for (AssignmentGroupItem i : assignmentGroups) {
            assignmentListModel.addElement(i.toString());
        }
    }

    private ArrayList<ArrayList<Individual>> getIndividualByUUIDForEachResultEntry(ArrayList<UUID> u) {
        ArrayList<ArrayList<Individual>> re = new ArrayList<>();
        for (int i = 0; i < u.size(); i++) {
            re.add(UUIDToIndividualsForEachResultEntry.get(u.get(i)));
        }
        return re;
    }

    private void calculateStatistic(String diagrammName) {
        int maxTimeStep = Integer.MIN_VALUE;
        for (EvacuationCellularAutomaton ca : selectedBatchResultEntry.getCa()) {
            maxTimeStep = Math.max(maxTimeStep, 1/**
             * ca.getTimeStep() + 1
             */
            );
        }
        ArrayList<ArrayList<ArrayList<Individual>>> IndividualPerAssignmentIndexToShow = new ArrayList<>();
        ArrayList<ArrayList<Individual>> IndividualsPerCycle;
        ArrayList<Individual> tmpIndividualsPerCycle;
        noIndividualsInAtLeastOneAssignmentIndex = false;
        for (Integer i : assignmentIndexToShow) {
            IndividualsPerCycle = new ArrayList<>();
            for (int j = 0; j < selectedBatchResultEntry.getCa().length; j++) {
                tmpIndividualsPerCycle = new ArrayList<>();
                for (String s : assignmentGroups.get(i).getAssignmentTypes()) {
                    //Set<Individual> temp = selectedBatchResultEntry.getCa()[j].getIndividualsInAssignmentType(selectedBatchResultEntry.getCa()[j].getAssignmentUUIS(s));
                    Set<Individual> temp = null;
                    if (temp == null) {
                        continue;
                    }
                    tmpIndividualsPerCycle.addAll(temp);
                }
                if (tmpIndividualsPerCycle.isEmpty()) {
                    continue;
                }
                IndividualsPerCycle.add(tmpIndividualsPerCycle);
            }
            if (IndividualsPerCycle.isEmpty()) {
                noIndividualsInAtLeastOneAssignmentIndex = true;
                continue;
            }
            IndividualPerAssignmentIndexToShow.add(IndividualsPerCycle);
        }
        if (noIndividualsInAtLeastOneAssignmentIndex) {
            if ((diagrammName.equals("Ausgangsverteilung")) || (diagrammName.equals("evakuierte Individuen in Prozent"))) {
                showDiagramm(statisticType.getSelectedItem().toString());
                diagramCategoryListModel.clear();
                diagramCategoryIndexToShow.clear();
                assignmentIndexToShow.clear();
            }
            return;
        }

        if (diagrammName.equals("Ausgangsverteilung")) {
            HashMap<String, Double> exitUtilization = new HashMap<>();
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                try {
                    exitUtilization = mccas.getTakenExit(IndividualPerAssignmentIndexToShow.get(i));
                } catch (OneIndNoPotentialException | IllegalArgumentException e) {
                }
            }
            int j = 0;
            for (String exitName : exitUtilization.keySet()) {
                j++;
                categoryDatasetValues.add(exitUtilization.get(exitName));
                categoryDatasetAssignments.add("(" + j + "): " + exitName);
            }
            showDiagramm(statisticType.getSelectedItem().toString());
            diagramCategoryListModel.clear();
            diagramCategoryIndexToShow.clear();
            assignmentIndexToShow.clear();
        }

        if (diagrammName.equals("Ankunftskurve")) {
            double income = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                dataset = new XYSeries(selectedBatchResultEntry.getName()
                        + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - "
                        + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));

                for (int c = 0; c <= maxTimeStep; c++) {
                    income = mccas.getNumberOfSafeIndividualForGroup(IndividualPerAssignmentIndexToShow.get(i), c);
                    dataset.add(c / currentAverageStepsPerSeconds, income);
                }
                datasetCollection.addSeries(dataset);
            }
        }

        if (diagrammName.equals("evakuierte Individuen in Prozent")) {
            double percent = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {

                percent = mccas.calculatePercentageOfSaveIndividuals(IndividualPerAssignmentIndexToShow.get(i));
                categoryDatasetValues.add(percent);
                categoryDatasetAssignments.add("evakuiert");
                categoryDatasetValues.add(100 - percent);
                categoryDatasetAssignments.add("nicht evakuiert");
            }
            showDiagramm(statisticType.getSelectedItem().toString());
            diagramCategoryListModel.clear();
            diagramCategoryIndexToShow.clear();
            assignmentIndexToShow.clear();
        }

        if (diagrammName.equals("maximale Blockadezeit")) {
            double waitedTime = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                waitedTime = 0.0;
                try {
                    waitedTime = mccas.calculateMaxWaitedTimeForGroup(IndividualPerAssignmentIndexToShow.get(i), maxTimeStep);
                } catch (GroupOfIndsNoPotentialException e) {
                }

                categoryDatasetValues.add(new Double(waitedTime / currentAverageStepsPerSeconds));

                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("minimale Blockadezeit")) {
            double waitedTime = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                waitedTime = 0.0;
                try {
                    waitedTime = mccas.calculateMinWaitedTimeForGroup(IndividualPerAssignmentIndexToShow.get(i), maxTimeStep);
                } catch (GroupOfIndsNoPotentialException e) {
                }

                categoryDatasetValues.add(new Double(waitedTime / currentAverageStepsPerSeconds));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("durchschnittliche Blockadezeit")) {
            double waitedTime = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                waitedTime = 0.0;
                try {
                    waitedTime = mccas.calculateAverageWaitedTimeForGroup(IndividualPerAssignmentIndexToShow.get(i), maxTimeStep);
                } catch (GroupOfIndsNoPotentialException e) {
                }

                categoryDatasetValues.add(new Double(waitedTime / currentAverageStepsPerSeconds));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("zurückgelegte Distanz")) {
            double coveredDistance = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                coveredDistance = 0.0;
                try {
                    coveredDistance = mccas.calculateAverageCoveredDistanceForGroup(IndividualPerAssignmentIndexToShow.get(i), maxTimeStep);
                } catch (GroupOfIndsNoPotentialException e) {
                }

                categoryDatasetValues.add(new Double(coveredDistance));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("minimale Distanz zum initialen Ausgang")) {
            double distanceToPlannedExit = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                distanceToPlannedExit = 0.0;
                try {
                    distanceToPlannedExit += mccas.minDistanceToPlannedExit(IndividualPerAssignmentIndexToShow.get(i));
                } catch (GroupOfIndsNoPotentialException e) {
                }

                categoryDatasetValues.add(distanceToPlannedExit);
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("minimale Distanz zum nächsten Ausgang")) {
            double distanceToNearestExit = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                distanceToNearestExit = 0.0;
                try {
                    distanceToNearestExit += mccas.minDistanceToNearestExit(IndividualPerAssignmentIndexToShow.get(i));
                } catch (GroupOfIndsNoPotentialException e) {
                }

                categoryDatasetValues.add(distanceToNearestExit);
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("maximale Zeit bis Safe")) {
            double safe = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                safe = 0.0;
                try {
                    safe = mccas.calculateMaxSafetyTimeForGroup(IndividualPerAssignmentIndexToShow.get(i));
                } catch (AllCyclesNoValueBecauseNotSafeException e) {
                }

                categoryDatasetValues.add(new Double(safe / currentAverageStepsPerSeconds));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("durchschnittliche Zeit bis Safe")) {
            double safe = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                safe = 0.0;
                try {
                    safe = mccas.calculateAverageSafetyTimeForGroup(IndividualPerAssignmentIndexToShow.get(i));
                } catch (AllCyclesNoValueBecauseNotSafeException e) {
                }

                categoryDatasetValues.add(new Double(safe / currentAverageStepsPerSeconds));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("minimale Zeit bis Safe")) {
            double safe = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                safe = 0.0;
                try {
                    safe = mccas.calculateMinSafetyTimeForGroup(IndividualPerAssignmentIndexToShow.get(i));
                } catch (AllCyclesNoValueBecauseNotSafeException e) {
                }

                categoryDatasetValues.add(new Double(safe / currentAverageStepsPerSeconds));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("Distanz über Zeit")) {
            ArrayList<Double> coveredDistance = new ArrayList<>();
            HashSet<Individual> tmp = new HashSet<>();
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                dataset = new XYSeries(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
                coveredDistance = new ArrayList<>();
                try {
                    coveredDistance = mccas.calculateAverageCoveredDistanceForGroupInTimeSteps(IndividualPerAssignmentIndexToShow.get(i), 0, maxTimeStep);
                } catch (GroupOfIndsNoPotentialException e) {
                    for (int c = 0; c <= maxTimeStep; c++) {
                        coveredDistance.add(new Double(0));
                    }
                }

                for (int c = 0; c <= maxTimeStep; c++) {
                    dataset.add(c / currentAverageStepsPerSeconds, coveredDistance.get(c));
                }

                datasetCollection.addSeries(dataset);
            }
        }

        if (diagrammName.equals("maximale Geschwindigkeit über Zeit")) {
            double speed = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                dataset = new XYSeries(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
                speed = 0.0;
                dataset.add(0, 0);
                for (int c = 1; c <= maxTimeStep; c++) {
                    try {
                        speed = mccas.calculateMaxSpeedForGroupInOneTimestep(IndividualPerAssignmentIndexToShow.get(i), c);
                    } catch (AllCyclesNoValueBecauseAlreadySafeException e) {
                        break;
                    }

                    dataset.add(c / currentAverageStepsPerSeconds, speed * currentAverageStepsPerSeconds);
                }
                datasetCollection.addSeries(dataset);
            }
        }

        if (diagrammName.equals("durschnittliche Geschwindigkeit über Zeit")) {
            double speed = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                dataset = new XYSeries(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
                dataset.add(0, 0);
                for (int c = 1; c <= maxTimeStep; c++) {
                    try {
                        speed = mccas.calculateAverageSpeedForGroupInOneTimestep(IndividualPerAssignmentIndexToShow.get(i), c);
                    } catch (AllCyclesNoValueBecauseAlreadySafeException e) {
                        break;
                    }

                    dataset.add(c / currentAverageStepsPerSeconds, speed * currentAverageStepsPerSeconds);
                }
                datasetCollection.addSeries(dataset);
            }
        }

        if (diagrammName.equals("maximale Geschwindigkeit")) {
            double maxSpeed = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                maxSpeed = 0.0;
                try {
                    maxSpeed = mccas.calculateAverageMaxSpeedForGroup(IndividualPerAssignmentIndexToShow.get(i), maxTimeStep);
                } catch (AllCyclesNoValueBecauseAlreadySafeException e) {
                }

                categoryDatasetValues.add(maxSpeed * currentAverageStepsPerSeconds);
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("durchschnittliche Geschwindigkeit")) {
            double avSpeed = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                avSpeed = 0.0;
                try {
                    avSpeed = mccas.calculateAverageAverageSpeedForGroup(IndividualPerAssignmentIndexToShow.get(i), maxTimeStep);
                } catch (AllCyclesNoValueBecauseAlreadySafeException e) {
                }

                categoryDatasetValues.add(new Double(avSpeed * currentAverageStepsPerSeconds));
                categoryDatasetAssignments.add(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
            }
        }

        if (diagrammName.equals("Panik über Zeit")) {
            double panic = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                dataset = new XYSeries(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
                dataset.add(0, 0);
                for (int c = 1; c <= maxTimeStep; c++) {
                    try {
                        panic = mccas.getPanicForGroup(IndividualPerAssignmentIndexToShow.get(i), c);
                    } catch (AllCyclesNoValueBecauseAlreadySafeException e) {
                        break;
                    }

                    dataset.add(c / currentAverageStepsPerSeconds, panic);
                }
                datasetCollection.addSeries(dataset);
            }
        }

        if (diagrammName.equals("Erschöpfung über Zeit")) {
            double exhaustion = 0.0;
            for (int i = 0; i < assignmentIndexToShow.size(); i++) {
                dataset = new XYSeries(selectedBatchResultEntry.getName() + (PropertyContainer.getGlobal().getAsBoolean("statistic.showAssignmentNamesInDiagrams") ? " - " + assignmentGroups.get(assignmentIndexToShow.get(i)).toString() : ""));
                exhaustion = 0.0;
                dataset.add(0, 0);
                for (int c = 1; c <= maxTimeStep; c++) {
                    try {
                        exhaustion = mccas.getExhaustionForGroup(IndividualPerAssignmentIndexToShow.get(i), c);
                    } catch (AllCyclesNoValueBecauseAlreadySafeException e) {
                        break;
                    }

                    dataset.add(c / currentAverageStepsPerSeconds, exhaustion);
                }
                datasetCollection.addSeries(dataset);
            }
        }
    }

    private void showDiagramm(String diagrammName) {
        if (diagrammName.equals("Grundinformationen")) {
            String[] columnNames = {"Bezeichnung", "Wert"};
            EvacuationCellularAutomaton tmpCA;
            int nrOfInd = 0;
            double evacSec = 0.0;
            double evacCAStep = 0;
            double notEvac = 0;
            double evac = 0;
            double notEvacNoExit = 0;
            double notEvacNoTime = 0;
            int bestEvacIndex = 0;
            int aveEvacIndex = 0;
            int worseEvacIndex = 0;
            TreeMap<Double, Integer> findMedian = new TreeMap<>();
            for (int i = 0; i < selectedBatchResultEntry.getCa().length; i++) {
                tmpCA = selectedBatchResultEntry.getCa()[i];
//                nrOfInd += tmpCA.getInitialIndividualCount();
//                evacSec += tmpCA.getSecondsPerStep() * tmpCA.getTimeStep();
//                evacCAStep += tmpCA.getTimeStep();
//                evac += tmpCA.getInitialIndividualCount() - tmpCA.deadIndividualsCount();
//                notEvac += tmpCA.deadIndividualsCount();
//                notEvacNoExit += tmpCA.getDeadIndividualCount(DeathCause.EXIT_UNREACHABLE);// getNrOfExitUnreachableDeadIndividuals();
//                notEvacNoTime += tmpCA.getDeadIndividualCount(DeathCause.NOT_ENOUGH_TIME);// getNrOfNotEnoughTimeDeadIndividuals();
//                findMedian.put(tmpCA.getTimeStep() * tmpCA.getSecondsPerStep(), i);
            }
            bestEvacIndex = findMedian.firstEntry().getValue();
            for (int j = 0; j < findMedian.size() / 2; j++) {
                findMedian.remove(findMedian.firstKey());
            }
            aveEvacIndex = findMedian.get(findMedian.firstKey());
            worseEvacIndex = findMedian.get(findMedian.lastKey());
            Object[][] data = {
                {"Informationen für Modell", selectedBatchResultEntry.getName()},
                {"Evakuierungszeit in Sekunden", evacSec / selectedBatchResultEntry.getCa().length},
                {"Evakuierungszeit in ZA-Schritten", evacCAStep / selectedBatchResultEntry.getCa().length},
                {"Anzahl Individuen", (double) nrOfInd / selectedBatchResultEntry.getCa().length},
                {"evakuiert", evac / selectedBatchResultEntry.getCa().length},
                {"nicht evakuiert", notEvac / selectedBatchResultEntry.getCa().length},
                {"nicht evakuiert weil kein Ausgang erreichbar", notEvacNoExit / selectedBatchResultEntry.getCa().length},
                {"nicht evakuiert weil die Zeit nicht gereicht hat", notEvacNoTime / selectedBatchResultEntry.getCa().length},
                //{"beste Evakuierungszeit (Durchlaufindex,Zeit)", ("(" + (bestEvacIndex + 1) + " - " + (selectedBatchResultEntry.getCa()[bestEvacIndex].getTimeStep() / selectedBatchResultEntry.getCa()[bestEvacIndex].getStepsPerSecond()) + ")")},
                //{"durchschnit. Evakuierungszeit (Durchlaufindex,Zeit)", ("(" + (aveEvacIndex + 1) + " - " + (selectedBatchResultEntry.getCa()[aveEvacIndex].getTimeStep() / selectedBatchResultEntry.getCa()[bestEvacIndex].getStepsPerSecond()) + ")")},
                //{"schlechteste Evakuierungszeit (Durchlaufindex,Zeit)", ("(" + (worseEvacIndex + 1) + " - " + (selectedBatchResultEntry.getCa()[worseEvacIndex].getTimeStep() / selectedBatchResultEntry.getCa()[bestEvacIndex].getStepsPerSecond()) + ")")}};
            };
            basicInformationTable = new JTable(data, columnNames);
            basicInformationScrollPane = new JScrollPane(basicInformationTable);
            diagrams.addTable(diagrammName, basicInformationScrollPane, west);
        }

        if ((noIndividualsInAtLeastOneAssignmentIndex) && !(diagrammName.equals("Grundinformationen"))) {
            chartData = new ChartData("bar", "NO INDIVIDUALS in at least one of the choosed dataset(s)", "", new ArrayList<>(), new ArrayList<>());
            evakuierungsdauer = ChartFactory.createBarChart("NO INDIVIDUALS in at least one of the choosed dataset(s)", "", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
            diagrams.addChart("NO INDIVIDUALS in at least one of the choosed dataset(s)", evakuierungsdauer, west);
        } else {

            if (diagrammName.equals("Ausgangsverteilung")) {
                chartData = new ChartData("pie", diagrammName + ":" + selectedBatchResultEntry.getName() + "-" + assignmentGroups.get(assignmentIndexToShow.get(0)).toString(), "Ausgänge", categoryDatasetValues, categoryDatasetAssignments);
                ausgangsverteilung = ChartFactory.createPieChart(diagrammName + ":" + selectedBatchResultEntry.getName() + "-" + assignmentGroups.get(assignmentIndexToShow.get(0)).toString(), ChartData.getPieDataSet(), false, true, false);
                diagrams.addChart(diagrammName, ausgangsverteilung, west);
            }

            if (diagrammName.equals("Ankunftskurve")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                ankunftskurve = ChartFactory.createXYLineChart(diagrammName, chartData.getYAxisLabel(), "Individuen", (XYDataset) datasetCollection, PlotOrientation.VERTICAL, true, true, false);
                diagrams.addChart(diagrammName, ankunftskurve, west);
            }

            if (diagrammName.equals("Evakuierungsdauer")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                evakuierungsdauer = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, evakuierungsdauer, west);
            }

            if (diagrammName.equals("evakuierte Individuen in Prozent")) {
                chartData = new ChartData("pie", diagrammName + ":" + selectedBatchResultEntry.getName() + "-" + assignmentGroups.get(assignmentIndexToShow.get(0)).toString(), "Individuen", categoryDatasetValues, categoryDatasetAssignments);
                evakuierteIndividueninProzent = ChartFactory.createPieChart(diagrammName + ":" + selectedBatchResultEntry.getName() + "-" + assignmentGroups.get(assignmentIndexToShow.get(0)).toString(), ChartData.getPieDataSet(), false, true, false);
                diagrams.addChart(diagrammName, evakuierteIndividueninProzent, west);
            }

            if (diagrammName.equals("maximale Blockadezeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                maxblockadezeit = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, maxblockadezeit, west);
            }

            if (diagrammName.equals("durchschnittliche Blockadezeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                aveblockadezeit = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, aveblockadezeit, west);
            }

            if (diagrammName.equals("minimale Blockadezeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                minblockadezeit = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, minblockadezeit, west);
            }

            if (diagrammName.equals("zurückgelegte Distanz")) {
                chartData = new ChartData("bar", diagrammName, "Meter [m]", categoryDatasetValues, categoryDatasetAssignments);
                zurueckgelegteDistanz = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, zurueckgelegteDistanz, west);
            }

            if (diagrammName.equals("minimale Distanz zum initialen Ausgang")) {
                chartData = new ChartData("bar", diagrammName, "Meter [m]", categoryDatasetValues, categoryDatasetAssignments);
                minimaleDistanzzuminitialenAusgang = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, minimaleDistanzzuminitialenAusgang, west);
            }

            if (diagrammName.equals("minimale Distanz zum nächsten Ausgang")) {
                chartData = new ChartData("bar", diagrammName, "Meter [m]", categoryDatasetValues, categoryDatasetAssignments);
                minimaleDistanzzumnaechstenAusgang = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, minimaleDistanzzumnaechstenAusgang, west);
            }

            if (diagrammName.equals("maximale Zeit bis Safe")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                maxZeitBisSafe = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, maxZeitBisSafe, west);
            }

            if (diagrammName.equals("durchschnittliche Zeit bis Safe")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                aveZeitBisSafe = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, aveZeitBisSafe, west);
            }

            if (diagrammName.equals("minimale Zeit bis Safe")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                minZeitBisSafe = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, minZeitBisSafe, west);
            }

            if (diagrammName.equals("Distanz über Zeit")) {
                chartData = new ChartData("bar", diagrammName, "Meter [m]", categoryDatasetValues, categoryDatasetAssignments);
                distanzueberZeit = ChartFactory.createXYLineChart(diagrammName, "Zeit [s]", chartData.getYAxisLabel(), (XYDataset) datasetCollection, PlotOrientation.VERTICAL, true, true, false);
                diagrams.addChart(diagrammName, distanzueberZeit, west);
            }

            if (diagrammName.equals("maximale Geschwindigkeit über Zeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                maximaleGeschwindigkeitueberZeit = ChartFactory.createXYLineChart(diagrammName, chartData.getYAxisLabel(), "Meter pro Sekunde [m/s]", (XYDataset) datasetCollection, PlotOrientation.VERTICAL, true, true, false);
                diagrams.addChart(diagrammName, maximaleGeschwindigkeitueberZeit, west);
            }

            if (diagrammName.equals("durschnittliche Geschwindigkeit über Zeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                durschnittlicheGeschwindigkeitueberZeit = ChartFactory.createXYLineChart(diagrammName, chartData.getYAxisLabel(), "Meter pro Sekunde [m/s]", (XYDataset) datasetCollection, PlotOrientation.VERTICAL, true, true, false);
                diagrams.addChart(diagrammName, durschnittlicheGeschwindigkeitueberZeit, west);
            }

            if (diagrammName.equals("maximale Geschwindigkeit")) {
                chartData = new ChartData("bar", diagrammName, "Meter pro Sekunde [m/s]", categoryDatasetValues, categoryDatasetAssignments);
                maximaleGeschwindigkeit = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, maximaleGeschwindigkeit, west);
            }

            if (diagrammName.equals("durchschnittliche Geschwindigkeit")) {
                chartData = new ChartData("bar", diagrammName, "Meter pro Sekunde [m/s]", categoryDatasetValues, categoryDatasetAssignments);
                durchschnittlicheGeschwindigkeit = ChartFactory.createBarChart(diagrammName, "Belegungen", chartData.getYAxisLabel(), chartData.getCDataSet(), PlotOrientation.VERTICAL, false, true, false);
                diagrams.addChart(diagrammName, durchschnittlicheGeschwindigkeit, west);
            }

            if (diagrammName.equals("Panik über Zeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                panik = ChartFactory.createXYLineChart(diagrammName, chartData.getYAxisLabel(), "Panik", (XYDataset) datasetCollection, PlotOrientation.VERTICAL, true, true, false);
                diagrams.addChart(diagrammName, panik, west);
            }

            if (diagrammName.equals("Erschöpfung über Zeit")) {
                chartData = new ChartData("bar", diagrammName, "Zeit [s]", categoryDatasetValues, categoryDatasetAssignments);
                erschoepfung = ChartFactory.createXYLineChart(diagrammName, chartData.getYAxisLabel(), "Erschöpfung", (XYDataset) datasetCollection, PlotOrientation.VERTICAL, true, true, false);
                diagrams.addChart(diagrammName, erschoepfung, west);
            }

        }//end else

        categoryDatasetValues = new ArrayList<>();
        categoryDatasetAssignments = new ArrayList<>();
        //dataset = new XYSeries("");
        datasetCollection = new XYSeriesCollection();
        diagrams.validate();
    }

    private void addComponents() {
        diagrams = new DiagramContainer(this);
        contentD = new JScrollPane(diagrams);
        west = new JPanel(new BorderLayout());
        west.add(contentD, BorderLayout.CENTER);

        properties = addPropertiesComponents();
        contentP = new JScrollPane(properties);
        east = new JPanel(new BorderLayout());
        east.add(contentP, BorderLayout.CENTER);
        east.setMinimumSize(new Dimension((int) properties.getMinimumSize().getWidth() + 22, (int) properties.getMinimumSize().getHeight()));
        east.setPreferredSize(east.getMinimumSize());
        east.setMaximumSize(east.getMinimumSize());

        pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pane.setLeftComponent(west);
        pane.setRightComponent(east);
        pane.setResizeWeight(1);
        //pane.resetToPreferredSizes();
        add(pane, BorderLayout.CENTER);
    }

    public class diagramClick extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent me) {
            Component comp = me.getComponent();
            String diagramName = "";
            if (comp instanceof JChartPanel) {
                JChartPanel c = (JChartPanel) comp;
                if (c.getChart() != null) {
                    diagramName = c.getChart().getTitle().getText();
                } else {
                    diagramName = "Grundinformationen";
                }

                diagramToRemove = (JPanel) c;
                statisticType.setSelectedItem(diagramName);
            } else if (comp instanceof JPanel) {
                diagramName = "Grundinformationen";

                diagramToRemove = (JPanel) comp;
                statisticType.setSelectedItem(diagramName);
            }
        }
    }

    private class AssignmentTypeListSelect implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            assignmentIndexToShow.clear();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    assignmentIndexToShow.add(i);
                }
            }
        }
    }

    private class DiagramCategoryListSelect implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            diagramCategoryIndexToShow.clear();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    diagramCategoryIndexToShow.add(i);
                }
            }
        }
    }

    private JPanel addPropertiesComponents() {
        JPanel p = new JPanel(new GridBagLayout());

        String[] statisticTypeNames = {"Grundinformationen",
            "Ausgangsverteilung", "Ankunftskurve",
            "evakuierte Individuen in Prozent", "maximale Blockadezeit",
            "durchschnittliche Blockadezeit", "minimale Blockadezeit",
            "zurückgelegte Distanz",
            "minimale Distanz zum initialen Ausgang",
            "minimale Distanz zum nächsten Ausgang", "Distanz über Zeit",
            "maximale Zeit bis Safe", "durchschnittliche Zeit bis Safe", "minimale Zeit bis Safe",
            "maximale Geschwindigkeit über Zeit",
            "durschnittliche Geschwindigkeit über Zeit",
            "maximale Geschwindigkeit",
            "durchschnittliche Geschwindigkeit", "Panik über Zeit", "Erschöpfung über Zeit"
        };
        statisticType = new JComboBox(statisticTypeNames);
        statisticType.addActionListener(new TypePerformed());

        JPanel timeIntervalGroup = new JPanel();
        timeInterval = new JLabel("Zeitintervall:");
        timeIntervalFrom = new JTextField(4);
        timeIntervalTo = new JTextField(4);
        timeIntervalGroup.add(timeInterval);
        timeIntervalGroup.add(timeIntervalFrom);
        timeIntervalGroup.add(new JLabel(" - "));
        timeIntervalGroup.add(timeIntervalTo);

        assignmentListModel = new DefaultListModel();
        assignmentList = new JList(assignmentListModel); // data has type Object[]
        assignmentList.getSelectionModel().addListSelectionListener(new AssignmentTypeListSelect());
        assignmentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        assignmentList.setLayoutOrientation(JList.VERTICAL);
        assignmentList.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(assignmentList);
        listScroller.setPreferredSize(new Dimension(100, 50));

        JPanel assignmentButtons = new JPanel();
        JButton btn_group = new JButton("Group");
        JButton btn_ungroup = new JButton("Ungroup");
        btn_group.addActionListener(new GroupPreformed());
        btn_ungroup.addActionListener(new UngroupPreformed());
        assignmentButtons.add(btn_group);
        assignmentButtons.add(btn_ungroup);

        JButton btn_createDiagram = new JButton("Create Diagram");
        btn_createDiagram.addActionListener(new CreateDiagramPerformed());

        JButton btn_removeDiagram = new JButton("Remove Diagram");
        btn_removeDiagram.addActionListener(new RemovePerformed());

        JPanel diagramButtonGroup = new JPanel();
        diagramButtonGroup.add(btn_createDiagram);
        diagramButtonGroup.add(btn_removeDiagram);

        diagramCategoryListModel = new DefaultListModel();
        diagramCategoryList = new JList(diagramCategoryListModel); // data has type
        // Object[]
        diagramCategoryList.getSelectionModel().addListSelectionListener(new DiagramCategoryListSelect());
        diagramCategoryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        diagramCategoryList.setLayoutOrientation(JList.VERTICAL);
        diagramCategoryList.setVisibleRowCount(-1);

        JScrollPane diagramCategoryListScroller = new JScrollPane(diagramCategoryList);
        diagramCategoryListScroller.setPreferredSize(new Dimension(100, 50));

        JPanel buttonGroup = new JPanel();
        JButton btn_addToDiagram = new JButton("Add to Diagram");
        JButton btn_removeFromDiagram = new JButton("Remove from Diagram");
        buttonGroup.add(btn_addToDiagram);
        buttonGroup.add(btn_removeFromDiagram);
        btn_addToDiagram.addActionListener(new AddToDiagramPerformed());
        btn_removeFromDiagram.addActionListener(new RemoveFromDiagramPerformed());

        // Create statisticSource at last because we need the GUI Objects above to initialize
        // our selection in BatchResultEntryComboBoxModel.setSelectedItem ()
        model = new BatchResultEntryComboBoxModel();
        statisticSource = new JComboBox(model);

        int y = 0;
        p.add(statisticType, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p.add(statisticSource, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p.add(new JLabel("Belegungen"), new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p.add(listScroller, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.5, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        p.add(assignmentButtons, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p.add(buttonGroup, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p.add(diagramCategoryListScroller, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.5, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        p.add(diagramButtonGroup, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        return p;
    }

    public JScrollPane getContentD() {
        return contentD;
    }
}

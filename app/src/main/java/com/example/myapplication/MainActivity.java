package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

public class MainActivity extends AppCompatActivity {
    public TextView result;
    private double a, b, accuracy;
    private ArrayList<Entry> lineEntries1,  lineEntries2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText A = findViewById(R.id.enterA);
        EditText B = findViewById(R.id.enterB);
        EditText ACCURACY = findViewById(R.id.enterAC);
        result = findViewById(R.id.textView7);
        Button count = findViewById(R.id.button);
         Objects.requireNonNull(getSupportActionBar()).setTitle("AMO_lab4");

        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    a = Double.parseDouble(String.valueOf(A.getText()));
                    b = Double.parseDouble(String.valueOf(B.getText()));
                    accuracy = Double.parseDouble(String.valueOf(ACCURACY.getText()));
                } catch (NumberFormatException numberFormatException) {
                    result.setText("Введіть коректні числа");
                }
                Double[] x = DoubleStream.iterate(a, n -> n + 0.1).limit((int) ((b - a) / 0.1) + 1).boxed().toArray(Double[]::new);
                LinkedHashSet<Double> results = new LinkedHashSet<>();
                for (int i = 1; i < x.length; i++) {
                    Double temp = chordMethod(x[i - 1], x[i], accuracy, 1000);
                    if (temp != null) {
                        results.add(temp);
                    }
                }
                results.stream().sorted();
                if (a >= b) {
                    result.setText("Нижня границя а має бути меньшою за верхню б");
                } else if (results.isEmpty()) {
                    result.setText("Метод хорд не гарантує збіжності на даному інтервалі");
                } else {
                    try {
                        result.setText(Arrays.toString(results.toArray()));
                    } catch (Exception exception) {
                        result.setText("POMILKA");
                    }
                }
            }
        });

       lineEntries1 = new ArrayList<Entry>();
       lineEntries2 = new ArrayList<Entry>();
        double[] funcLinesX = new double[40];
        double[] funcLinesY = new double[40];
        funcLinesX[0] = 0.1;
        funcLinesY[0] = -1.05;
        for (int i = 1; i < funcLinesX.length; i++) {
            funcLinesX[i] = funcLinesX[i - 1] + 0.2;
            funcLinesY[i] = (2*Math.log10(funcLinesX[i]) - funcLinesX[i]/2 + 1);
        }
        for (int i = 0; i < funcLinesX.length; i++) {
            lineEntries1.add(new Entry((float) funcLinesX[i], (float) funcLinesY[i]));
        }
        for (int i = 0; i < funcLinesX.length; i++) {
            lineEntries2.add(new Entry((float) funcLinesX[i], 0));
        }

        drawLineChart1();
    }

    public  double f(double x) {
        return 2*Math.log10(x) - x/2 + 1;
    }

    public Double chordMethod(Double a, Double b, Double tol, int maxIter) {
        Double fa = f(a);
        Double fb = f(b);

        if (fa * fb >= 0) {
          return null;
        }

        for (int i = 0; i < maxIter; i++) {
            Double c = (a * f(b) - b * f(a)) / (f(b) - f(a));
            Double fc = f(c);
            if (Math.abs(fc) < tol) {
                return c;
            }
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }
        return null;
    }

    private void drawLineChart1() {
        LineChart lineChart = findViewById(R.id.lineChart2);
        List<Entry> lineEntries = lineEntries1;
        List<Entry> lineEntries3 = lineEntries2;
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet lineDataSet1 = new LineDataSet(lineEntries, "ФУНКЦІЯ");
        LineDataSet lineDataSet2 = new LineDataSet(lineEntries3, "y=0");
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setCircleColor(Color.BLUE);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setCircleRadius(6);
        lineDataSet1.setCircleHoleRadius(3);
        lineDataSet1.setDrawHighlightIndicators(false);
        lineDataSet1.setHighLightColor(Color.BLUE);
        lineDataSet1.setValueTextSize(12);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setValueTextColor(Color.DKGRAY);

        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet2.setLineWidth(2);
        lineDataSet2.setColor(Color.GREEN);
        lineDataSet2.setCircleColor(Color.GREEN);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setCircleRadius(6);
        lineDataSet2.setCircleHoleRadius(3);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setHighLightColor(Color.GREEN);
        lineDataSet2.setValueTextSize(12);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setValueTextColor(Color.DKGRAY);

        LineData lineData = new LineData(dataSets);
        lineChart.getDescription().setText("f(x)");
        lineChart.getDescription().setTextSize(12);
        lineChart.setDrawMarkers(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateY(1000);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(0.5f);
        lineChart.getXAxis().setLabelCount(lineDataSet1.getEntryCount());
        lineChart.setData(lineData);
    }
}
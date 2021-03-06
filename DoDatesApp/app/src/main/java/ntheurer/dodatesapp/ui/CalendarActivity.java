package ntheurer.dodatesapp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ntheurer.dodatesapp.R;
import ntheurer.dodatesapp.model.Assignment;
import ntheurer.dodatesapp.model.SingletonModel;
import ntheurer.dodatesapp.model.UserClass;

public class CalendarActivity extends AppCompatActivity {

    private final String tag = "CalendarActivity";
    private Context context;
    private String dateSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(tag, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        SingletonModel sModel = SingletonModel.getInstance();

//        setupForTesting();

        dateSelected = "11/12/2018";
        refreshRecyclerView();

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.w(tag, "date changed");
                dateSelected = month + "/" + dayOfMonth + "/" + year;
                refreshRecyclerView();
            }
        });

        context = this;

        Button viewClassListButton = (Button) findViewById(R.id.viewClassListButton);
        viewClassListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(tag, "viewClassListButton clicked");
                Intent myIntent = new Intent(CalendarActivity.this, ClassListActivity.class);
                CalendarActivity.this.startActivity(myIntent);
            }
        });
    }

    private void refreshRecyclerView() {
        Log.w(tag, "refreshRecyclerView entered");
        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;
        SingletonModel sModel = SingletonModel.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.calendar_assignment_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        sModel.updateAssignmentByDateMap();
        Map<String, List<Assignment>> assignmentByDateMap = sModel.getAssignmentByDateMap();
        Log.w(tag, "size of assignmentByDateMap = " + assignmentByDateMap.size());
        List<Assignment> listOfAssignmentsToAdd = assignmentByDateMap.get(dateSelected);
        if (listOfAssignmentsToAdd == null) {
            Log.e(tag, "listOfAssignmentsToAdd is null");
            Log.w(tag, "dateSelected: " + dateSelected);
            Log.w(tag, "assignmentByDateMap.keySet() = " + assignmentByDateMap.keySet());
        }
        else {
            Log.w(tag, "size of listOfAssignmentsToAdd = " + listOfAssignmentsToAdd.size());
        }
        adapter = new CalendarActivity.RecyclerAdapter(listOfAssignmentsToAdd);
        recyclerView.setAdapter(adapter);
        Log.w(tag, "refreshRecyclerView about to return");
    }

    /*private void setupForTesting() {
        SingletonModel sModel = SingletonModel.getInstance();
        UserClass userClass = new UserClass("Math", (sModel.getColorList()).get(1));
        Assignment assignment = new Assignment("written", "11/12/2018", userClass);
        userClass.addSingleAssignment(assignment);
        sModel.addClass(userClass);

        UserClass userClass2 = new UserClass("Science", (sModel.getColorList()).get(2));
        Assignment assignment2 = new Assignment("Online", "11/13/2018", userClass2);
        userClass2.addSingleAssignment(assignment2);
        Assignment assignment3 = new Assignment("Test", "11/12/2018", userClass2);
        userClass2.addSingleAssignment(assignment3);
        Assignment assignment4 = new Assignment("Quiz", "11/13/2018", userClass2);
        userClass2.addSingleAssignment(assignment4);
        sModel.addClass(userClass2);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class AssignmentListHolder extends RecyclerView.ViewHolder {
        private TextView assignmentNameTextView;
        private TextView assignmentDueDateTextView;

        public AssignmentListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.assignment_recycler_item, parent, false));
            Log.w(tag, "AssignmentListHolder contrustor entered");
            assignmentNameTextView = (TextView) itemView.findViewById(R.id.recycler_assignment_name);
            assignmentDueDateTextView = (TextView) itemView.findViewById(R.id.recycler_assignment_due_date);
        }

        public void bind(Assignment currAssignment) {
            SingletonModel sModel = SingletonModel.getInstance();
            String dueDate = "Due: ";

            if (assignmentNameTextView == null) {
                Log.e(tag, "assignmentNameTextView == null");
            }

            if (assignmentDueDateTextView == null) {
                Log.e(tag, "assignmentDueDateTextView == null");
            }

            try {
                assignmentNameTextView.setText(currAssignment.getAssignmentName());
                UserClass userClass = currAssignment.getUserClass();
                if (userClass == null) {
                    Log.e(tag, "assignment's user class is null");
                }
                String classColor = userClass.getColorString();
                if (classColor == null) {
                    Log.e(tag, "class color is null");
                }
                else {
                    Log.w(tag, "class color is " + classColor);
                }
                assignmentNameTextView.setTextColor(Color.parseColor(sModel.getColorMap().get(classColor)));;
                dueDate = dueDate + currAssignment.getDueDate();
                Log.w(tag, "dueDate = \"" + dueDate + "\"");
                assignmentDueDateTextView.setText(dueDate);
                assignmentDueDateTextView.setTextColor(Color.parseColor(sModel.getColorMap().get(classColor)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<CalendarActivity.AssignmentListHolder> {
        private List<Assignment> assignmentList;

        public RecyclerAdapter(List<Assignment> assignments) {
            assignmentList = assignments;
        }

        @Override
        public CalendarActivity.AssignmentListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.w(tag, "entered RecyclerAdapter contructor");
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            return new CalendarActivity.AssignmentListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CalendarActivity.AssignmentListHolder holder, int position) {
            Assignment assignment = assignmentList.get(position);
            Log.i(tag, "assignment position = " + position);
            Log.i(tag, "assignment name = " + assignment.getAssignmentName());
            holder.bind(assignment);
        }

        @Override
        public int getItemCount() {
            if (assignmentList == null) {
                return 0;
            }
            return assignmentList.size();
        }
    }

}

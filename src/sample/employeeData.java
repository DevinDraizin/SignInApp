package sample;

import java.util.ArrayList;
import java.util.Date;


class employee implements Comparable<employee>
{
    String ID;
    String firstName;
    String lastName;
    String salary;
    String phone;
    String notes;
    String email;
    String showNotes;
    Boolean notify;
    int active; //1 = Active and 0 = Inactive
    Date lastPaid;

    ArrayList<ArrayList<timeBlock>> timeSheet;

    public void setID(String ID) {
        this.ID = ID;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    void setSalary(String salary) {
        this.salary = salary;
    }

    void setPhone(String phone) {
        this.phone = phone;
    }

    void setEmail(String email) {
        this.email = email;
    }


    public employee()
    {
        this.firstName = null;
        this.lastName = null;
        this.ID = null;
        this.notes = null;
        this.salary = null;
        this.email = null;
        this.phone = null;
        this.showNotes = "";
        this.timeSheet = new ArrayList<>();
        this.notify = false;
        this.lastPaid = null;
    }


    @Override
    public int compareTo(employee o)
    {
        return (this.firstName + this.lastName).compareTo(o.firstName + o.lastName);
    }
}



class timeBlock
{
    Date in;
    Date out;
    Boolean start;
    Boolean end;
    Boolean err;

    double totalHours;

    timeBlock()
    {
        this.in = null;
        this.out = null;
        totalHours = 0;
        start = false;
        end = false;
        err = false;
    }

    timeBlock(Date in, Date out)
    {
        this.in = in;
        this.out = out;
        totalHours = 0;
        start = false;
        end = false;
        err = false;
    }


    void calcTime()
    {

        //This totally works with daylights savings and leap years. probably
        if(out != null && in != null)
        {
            this.totalHours = (double)(out.getTime() - in.getTime())/(1000 * 60 * 60);
        }
    }
}

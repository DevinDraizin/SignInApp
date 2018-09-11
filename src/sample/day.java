package sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class day
{
    Date signIn;
    ArrayList<String> breakIn;
    ArrayList<String> breakOut;
    Date SignOut;

    public Date getSignIn() {
        return signIn;
    }

    public void setSignIn(Date signIn) {
        this.signIn = signIn;
    }

    public String getBreakIn() {
        return breakIn.stream().collect(Collectors.joining("\n\n"));
    }

    public void setBreakIn(ArrayList<String> breakIn) {
        this.breakIn = breakIn;
    }

    public String getBreakOut() {
        return breakOut.stream().collect(Collectors.joining("\n\n"));
    }

    public void setBreakOut(ArrayList<String> breakOut) {
        this.breakOut = breakOut;
    }

    public Date getSignOut() {
        return SignOut;
    }

    public void setSignOut(Date signOut) {
        SignOut = signOut;
    }

    public day(Date signIn, ArrayList<String> breakIn, ArrayList<String> breakOut, Date signOut) {
        this.signIn = signIn;
        this.breakIn = breakIn;
        this.breakOut = breakOut;
        this.SignOut = signOut;
    }

    public day()
    {
        this.signIn = null;
        this.breakIn = null;
        this.breakOut = null;
        this.SignOut = null;
    }
}

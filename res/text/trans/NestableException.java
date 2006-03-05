// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NestableException.java

package org.apache.commons.lang.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

// Referenced classes of package org.apache.commons.lang.exception:
//            NestableDelegate, Nestable

public class NestableException extends Exception
    implements Nestable
{

    public NestableException()
    {
        _flddelegate = new NestableDelegate(this);
        cause = null;
    }

    public NestableException(String msg)
    {
        super(msg);
        _flddelegate = new NestableDelegate(this);
        cause = null;
    }

    public NestableException(Throwable cause)
    {
        _flddelegate = new NestableDelegate(this);
        this.cause = null;
        this.cause = cause;
    }

    public NestableException(String msg, Throwable cause)
    {
        super(msg);
        _flddelegate = new NestableDelegate(this);
        this.cause = null;
        this.cause = cause;
    }

    public Throwable getCause()
    {
        return cause;
    }

    public String getMessage()
    {
        if(super.getMessage() != null)
            return super.getMessage();
        if(cause != null)
            return cause.toString();
        else
            return null;
    }

    public String getMessage(int index)
    {
        if(index == 0)
            return super.getMessage();
        else
            return _flddelegate.getMessage(index);
    }

    public String[] getMessages()
    {
        return _flddelegate.getMessages();
    }

    public Throwable getThrowable(int index)
    {
        return _flddelegate.getThrowable(index);
    }

    public int getThrowableCount()
    {
        return _flddelegate.getThrowableCount();
    }

    public Throwable[] getThrowables()
    {
        return _flddelegate.getThrowables();
    }

    public int indexOfThrowable(Class type)
    {
        return _flddelegate.indexOfThrowable(type, 0);
    }

    public int indexOfThrowable(Class type, int fromIndex)
    {
        return _flddelegate.indexOfThrowable(type, fromIndex);
    }

    public void printStackTrace()
    {
        _flddelegate.printStackTrace();
    }

    public void printStackTrace(PrintStream out)
    {
        _flddelegate.printStackTrace(out);
    }

    public void printStackTrace(PrintWriter out)
    {
        _flddelegate.printStackTrace(out);
    }

    public final void printPartialStackTrace(PrintWriter out)
    {
        super.printStackTrace(out);
    }

    protected NestableDelegate _flddelegate;
    private Throwable cause;
}

package org.dbunit.dataset.common.handlers;

import java.util.List;

import mockmaker.ExceptionalReturnValue;
import mockmaker.ReturnValues;
import mockmaker.VoidReturnValues;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.ExpectationList;

/**
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.4.0
 */
public class MockHandler extends AbstractPipelineComponent{
   private ExpectationCounter mySetSuccessorCalls = new ExpectationCounter("org.dbunit.dataset.csv.handlers.AbstractPipelineComponent SetSuccessorCalls");
   private ReturnValues myActualSetSuccessorReturnValues = new VoidReturnValues(false);
   private ExpectationList mySetSuccessorParameter0Values = new ExpectationList("org.dbunit.dataset.csv.handlers.AbstractPipelineComponent org.dbunit.dataset.csv.handlers.Handler");
   private ExpectationCounter myGetSinkCalls = new ExpectationCounter("org.dbunit.dataset.csv.handlers.AbstractPipelineComponent GetSinkCalls");
   private ReturnValues myActualGetSinkReturnValues = new ReturnValues(false);
   private ExpectationCounter myHandleCalls = new ExpectationCounter("org.dbunit.dataset.csv.handlers.AbstractPipelineComponent HandleCalls");
   private ReturnValues myActualHandleReturnValues = new VoidReturnValues(false);
   private ExpectationList myHandleParameter0Values = new ExpectationList("org.dbunit.dataset.csv.handlers.AbstractPipelineComponent char");
   public void setExpectedSetSuccessorCalls(int calls){
      mySetSuccessorCalls.setExpected(calls);
   }
   public void addExpectedSetSuccessorValues(Handler arg0){
      mySetSuccessorParameter0Values.addExpected(arg0);
   }
   public void setSuccessor(PipelineComponent arg0){
      super.setSuccessor(arg0);
      mySetSuccessorCalls.inc();
      mySetSuccessorParameter0Values.addActual(arg0);
      Object nextReturnValue = myActualSetSuccessorReturnValues.getNext();
      if (nextReturnValue instanceof ExceptionalReturnValue && ((ExceptionalReturnValue)nextReturnValue).getException() instanceof RuntimeException)
          throw (RuntimeException)((ExceptionalReturnValue)nextReturnValue).getException();
   }
   public void setupExceptionSetSuccessor(Throwable arg){
      myActualSetSuccessorReturnValues.add(new ExceptionalReturnValue(arg));
   }
   public void setExpectedGetSinkCalls(int calls){
      myGetSinkCalls.setExpected(calls);
   }
   public void setupExceptionGetSink(Throwable arg){
      myActualGetSinkReturnValues.add(new ExceptionalReturnValue(arg));
   }
   public void setupGetSink(List arg){
      myActualGetSinkReturnValues.add(arg);
   }
   public void setExpectedHandleCalls(int calls){
      myHandleCalls.setExpected(calls);
   }
   public void addExpectedHandleValues(char arg0){
      myHandleParameter0Values.addExpected(new Character(arg0));
   }
   public void handle(char arg0) throws IllegalInputCharacterException, PipelineException{
      myHandleCalls.inc();
      myHandleParameter0Values.addActual(new Character(arg0));
      Object nextReturnValue = myActualHandleReturnValues.getNext();
      if (nextReturnValue instanceof ExceptionalReturnValue && ((ExceptionalReturnValue)nextReturnValue).getException() instanceof IllegalInputCharacterException)
          throw (IllegalInputCharacterException)((ExceptionalReturnValue)nextReturnValue).getException();
      if (nextReturnValue instanceof ExceptionalReturnValue && ((ExceptionalReturnValue)nextReturnValue).getException() instanceof RuntimeException)
          throw (RuntimeException)((ExceptionalReturnValue)nextReturnValue).getException();
       super.handle(arg0);
   }
   public void setupExceptionHandle(Throwable arg){
      myActualHandleReturnValues.add(new ExceptionalReturnValue(arg));
   }
   public void verify(){
      mySetSuccessorCalls.verify();
      mySetSuccessorParameter0Values.verify();
      myGetSinkCalls.verify();
      myHandleCalls.verify();
      myHandleParameter0Values.verify();
   }
	public boolean canHandle(char c) throws IllegalInputCharacterException {
		return false;
	}

}

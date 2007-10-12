/*
  File: SynchronizedInt.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  19Jun1998  dl               Create public version
*/

package org.dbunit.util.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class useful for offloading synch for int instance variables.
 *
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 **/

public class SynchronizedInt extends SynchronizedVariable implements Comparable, Cloneable {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedInt.class);

  protected int value_;

  /** 
   * Make a new SynchronizedInt with the given initial value,
   * and using its own internal lock.
   **/
  public SynchronizedInt(int initialValue) { 
    super(); 
    value_ = initialValue; 
  }

  /** 
   * Make a new SynchronizedInt with the given initial value,
   * and using the supplied lock.
   **/
  public SynchronizedInt(int initialValue, Object lock) { 
    super(lock); 
    value_ = initialValue; 
  }

  /** 
   * Return the current value 
   **/
  public final int get() {
        logger.debug("get() - start");
 synchronized(lock_) { return value_; } }

  /** 
   * Set to newValue.
   * @return the old value 
   **/

  public int set(int newValue) {
        logger.debug("set(newValue=" + newValue + ") - start");
 
    synchronized (lock_) {
      int old = value_;
      value_ = newValue; 
      return old;
    }
  }

  /**
   * Set value to newValue only if it is currently assumedValue.
   * @return true if successful
   **/
  public boolean commit(int assumedValue, int newValue) {
        logger.debug("commit(assumedValue=" + assumedValue + ", newValue=" + newValue + ") - start");

    synchronized(lock_) {
      boolean success = (assumedValue == value_);
      if (success) value_ = newValue;
      return success;
    }
  }

  /** 
   * Atomically swap values with another SynchronizedInt.
   * Uses identityHashCode to avoid deadlock when
   * two SynchronizedInts attempt to simultaneously swap with each other.
   * (Note: Ordering via identyHashCode is not strictly guaranteed
   * by the language specification to return unique, orderable
   * values, but in practice JVMs rely on them being unique.)
   * @return the new value 
   **/

  public int swap(SynchronizedInt other) {
        logger.debug("swap(other=" + other + ") - start");

    if (other == this) return get();
    SynchronizedInt fst = this;
    SynchronizedInt snd = other;
    if (System.identityHashCode(fst) > System.identityHashCode(snd)) {
      fst = other;
      snd = this;
    }
    synchronized(fst.lock_) {
      synchronized(snd.lock_) {
        fst.set(snd.set(fst.get()));
        return get();
      }
    }
  }

  /** 
   * Increment the value.
   * @return the new value 
   **/
  public int increment() {
        logger.debug("increment() - start");
 
    synchronized (lock_) {
      return ++value_; 
    }
  }

  /** 
   * Decrement the value.
   * @return the new value 
   **/
  public int decrement() {
        logger.debug("decrement() - start");
 
    synchronized (lock_) {
      return --value_; 
    }
  }

  /** 
   * Add amount to value (i.e., set value += amount)
   * @return the new value 
   **/
  public int add(int amount) {
        logger.debug("add(amount=" + amount + ") - start");
 
    synchronized (lock_) {
      return value_ += amount; 
    }
  }

  /** 
   * Subtract amount from value (i.e., set value -= amount)
   * @return the new value 
   **/
  public int subtract(int amount) {
        logger.debug("subtract(amount=" + amount + ") - start");
 
    synchronized (lock_) {
      return value_ -= amount; 
    }
  }

  /** 
   * Multiply value by factor (i.e., set value *= factor)
   * @return the new value 
   **/
  public synchronized int multiply(int factor) {
        logger.debug("multiply(factor=" + factor + ") - start");
 
    synchronized (lock_) {
      return value_ *= factor; 
    }
  }

  /** 
   * Divide value by factor (i.e., set value /= factor)
   * @return the new value 
   **/
  public int divide(int factor) {
        logger.debug("divide(factor=" + factor + ") - start");
 
    synchronized (lock_) {
      return value_ /= factor; 
    }
  }

  /** 
   * Set the value to the negative of its old value
   * @return the new value 
   **/
  public  int negate() {
        logger.debug("negate() - start");
 
    synchronized (lock_) {
      value_ = -value_;
      return value_;
    }
  }

  /** 
   * Set the value to its complement
   * @return the new value 
   **/
  public  int complement() {
        logger.debug("complement() - start");
 
    synchronized (lock_) {
      value_ = ~value_;
      return value_;
    }
  }

  /** 
   * Set value to value &amp; b.
   * @return the new value 
   **/
  public  int and(int b) {
        logger.debug("and(b=" + b + ") - start");
 
    synchronized (lock_) {
      value_ = value_ & b;
      return value_;
    }
  }

  /** 
   * Set value to value | b.
   * @return the new value 
   **/
  public  int or(int b) {
        logger.debug("or(b=" + b + ") - start");
 
    synchronized (lock_) {
      value_ = value_ | b;
      return value_;
    }
  }


  /** 
   * Set value to value ^ b.
   * @return the new value 
   **/
  public  int xor(int b) {
        logger.debug("xor(b=" + b + ") - start");
 
    synchronized (lock_) {
      value_ = value_ ^ b;
      return value_;
    }
  }

  public int compareTo(int other) {
        logger.debug("compareTo(other=" + other + ") - start");

    int val = get();
    return (val < other)? -1 : (val == other)? 0 : 1;
  }

  public int compareTo(SynchronizedInt other) {
        logger.debug("compareTo(other=" + other + ") - start");

    return compareTo(other.get());
  }

  public int compareTo(Object other) {
        logger.debug("compareTo(other=" + other + ") - start");

    return compareTo((SynchronizedInt)other);
  }

  public boolean equals(Object other) {
        logger.debug("equals(other=" + other + ") - start");

    if (other != null &&
        other instanceof SynchronizedInt)
      return get() == ((SynchronizedInt)other).get();
    else
      return false;
  }

  public int hashCode() {
        logger.debug("hashCode() - start");
 return get(); }

  public String toString() {
        logger.debug("toString() - start");
 return String.valueOf(get()); }

}


package com.verknowsys

package object dbbench {
    implicit def int2times(i: Int) = new { 
        def times(f: => Unit) = (1 to i) foreach(e => f) 
    }
}

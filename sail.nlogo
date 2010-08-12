
globals [tack-distance  mark-radius last-wind-angle last-pointing-angle]
breed [boats boat]
breed [marks mark]
breed [windvane wv]

boats-own [target should-tack mark-number mark-roundings last-tack-pos last-pos]


to setup
   clear-all
   set-default-shape marks "house"
   set tack-distance 10 ; * tack-length
   set mark-radius 1
   set last-wind-angle -3
   set last-pointing-angle -3
   ;;set pointing-angle 45
   create-ordered-marks how-many-marks [fd .75 * max-pxcor]
   create-boats how-many-boats [
                setxy (random (.1 * max-pxcor) + 0 ) (random (.1 * max-pycor) +( -.75 * max-pycor))  
                set target mark 0
                set mark-roundings 0
                set mark-number 0
                set should-tack true
                set last-tack-pos patch-here
                
   ]  
   draw-wind-vane
   
end

to go 
   if not all? boats [have-finished] [
     ask boats [  
       if not have-finished [
         tack-go tack-length
         let foo handle-mark-rounding tack-length
       ]
     ]
   ]
   
   draw-wind-vane
end


to tack-go [ distancet ]
  ifelse can-point towards target [
    set heading towards target 
    forward distancet
  ]  
  [
    possible-tack
    forward distancet
  ]  
end

to possible-tack 
  let corrected-wind normalize(wind-angle - 180)
 
  if distance last-tack-pos > tack-distance or last-pos = patch-here [
    set last-tack-pos patch-here
    ifelse should-tack [
      set heading corrected-wind + pointing-angle
      ;show "tacking"
      set should-tack false
    ]
    [
      set heading corrected-wind - pointing-angle
      set should-tack true
    ]
  ]
  set last-pos patch-here
end

to-report normalize [angle-measure]
 report angle-measure mod 360
    
end
to-report can-point [heading-angle]
  let corrected-wind normalize(wind-angle - 180)
  let angle-to-wind normalize  abs (abs(heading-angle - wind-angle) - 180)
   ;show "wind-angle heading angle, angle-to-wind, corrected-wind"
   ;type wind-angle type " " type heading-angle type " " type  angle-to-wind type " " type corrected-wind
   report angle-to-wind > pointing-angle 
end

to-report handle-mark-rounding [distancet] 
   ifelse will-round-mark distancet [
       set mark-roundings mark-roundings + 1
       set mark-number mark-roundings mod how-many-marks
       set target mark mark-number
       report true 
   ]
   [ report false ]
end



to-report will-round-mark [distancet] ;; boat method
   report distance target < mark-radius ; and ( heading = towards target or normalize (towards target - 180) = heading)
    end

to-report have-finished 
 report mark-roundings >= (laps * how-many-marks)
end



to draw-wind-vane 
    if (not (last-wind-angle = wind-angle)) or (not (last-pointing-angle = pointing-angle)) [
    ask  windvane  [
    die
    ]
      create-windvane 1 [
    setxy (.75 * max-pxcor) (.75 * max-pycor)
    set heading wind-angle
    pd
    fd 5
    lt pointing-angle
    fd 2
    back 2
    rt 2 * pointing-angle
    fd 2
    back 2
    set heading wind-angle 
    set last-wind-angle wind-angle
    set last-pointing-angle pointing-angle
    ]]

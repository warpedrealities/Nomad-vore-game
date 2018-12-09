
function seekAngle(current,target)
	local se=current-target

	if (se>4) then
		se=se-8
	end
	if (se<-4) then
	se=8+se
	end

	return se;
end

function approach(script,sense,s)
	if (s>0.25) then
		script:setCourse(-1,1)
	end
	if (s<-0.25) then
		script:setCourse(1,1)
	end		
	if (s>-0.25 and s<0.25) then
		script:setCourse(0,1)
	end	
end

function broadsideLeft(script,sense,s)
	print("broadsideLeft")
	if (s>2) then
		script:setCourse(-1,1)
	end
	if (s<2) then
		script:setCourse(1,1)	
	end
	script:fire(0,0)
end

function broadsideRight(script,sense,s)
	print("broadsideRight")
	if (s>-2) then
		script:setCourse(-1,1)
	end
	if (s<-2) then
		script:setCourse(1,1)	
	end
	script:fire(1,0)
end

function flee(s,script, sense)
	v=script:getValue(0)
	if (v==0) then
		script:setCourse(0,2)		
	else
		if (v==1) then
			if (s>0) then
				script:setCourse(-1,1)
				script:setValue(1,1)
			end
			if (s<0) then
				script:setCourse(1,1)
				script:setValue(1,2)
			end			
		else
			if (script:getValue(1)==1) then
				script:setValue(1,1)
			else
				script:setValue(1,2)
			end
		end
	end
	v=v+1
	if (v>7) then
		v=0
	end
	script:setValue(0,v)
end

function main(script,sense)  
	
	angle=sense:getAngleTo(0)
	course=sense:getHeading()
	distance=sense:getDistanceTo(0)
	s=seekAngle(course,angle)
	if (distance>10) then
		approach(script,sense,s)
	else
		if (s>3 or s<-3) then
			flee(s,script,sense)
			
		end
		if (s<3 and s>0) then
			broadsideLeft(script,sense,s)
		end
		if (s>-3 and s<0) then
			broadsideRight(script,sense,s)
		end
	end
	
end  
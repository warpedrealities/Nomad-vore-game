
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

function main(script,sense)  

	angle=sense:getAngleTo(0)
	course=sense:getHeading()
	distance=sense:getDistanceTo(0)
	s=seekAngle(course,angle)

	if (s>-0.25 and s<0.25) then
		script:setCourse(0,1)
	end
	if (s>-0.5 and s<0.5) then
		script:fire(0,0)
	end
	if (s>0.25) then
		script:setCourse(-1,1)

	end
	if (s<-0.25) then
		script:setCourse(1,1)
	end
	
	script:activateShield()
	
end  
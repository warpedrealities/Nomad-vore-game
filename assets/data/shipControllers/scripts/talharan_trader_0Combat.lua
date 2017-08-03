
function seekAngle(current,target)
	local se=current-target
	print("raw angle"..se)
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
	print("target distance "..distance)
	print("course "..course)
	print("target "..angle)
	print("difference "..s)
	if (s>-0.25 and s<0.25) then
		script:setCourse(0,1)
		script:fire(0,0)
		print("fly forwards")
	end
	if (s>0.25) then
		script:setCourse(-1,1)
		print("fly left")
	end
	if (s<-0.25) then
		script:setCourse(1,1)
		print("fly right")
	end
	
	
end  
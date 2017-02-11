
function robot(controllable,sense)
	a=controllable:getValue(0)
	if (a>0 and a<10) then
	controllable:move(0);
	end
	if (a>10 and a<20) then
	controllable:move(2);
	end	
	if (a>20 and a<30) then
	controllable:move(4);
	end	
	if (a>30 and a<40) then
	controllable:move(6);
	end		
	a=a+1
	if (a>40) then
	a=0
	end
	
	controllable:setValue(0,a)
end


function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
	robot(controllable,sense)
	end
end  
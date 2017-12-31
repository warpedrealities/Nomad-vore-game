
function turn(controllable,sense)
	a=controllable:getValue(0)
	a=a+1;
	if (a>=4) then
		a=0;
	end
	controllable:setValue(0,a)
end

function roamN(controllable,sense,pos)
	if sense:CanWalk(pos.x,pos.y+1)==false then
		turn(controllable,sense)
	else
			controllable:move(0)
	end

end

function roamE(controllable,sense,pos)
	if sense:CanWalk(pos.x+1,pos.y)==false then
		turn(controllable,sense)
	else
			controllable:move(2)
	end

end

function roamS(controllable,sense,pos)
	if sense:CanWalk(pos.x,pos.y-1)==false then
		turn(controllable,sense)
	else
			controllable:move(4)
	end

end

function roamW(controllable,sense,pos)
	if sense:CanWalk(pos.x-1,pos.y)==false then
		turn(controllable,sense)
	else
			controllable:move(6)
	end
end

function robot(controllable,sense)
	pos=controllable:getPosition()
	a=controllable:getValue(0)
	if (a==0) then
	roamN(controllable,sense,pos)
	end
	if (a==1) then
	roamE(controllable,sense,pos)
	end
	if (a==2) then
	roamS(controllable,sense,pos)
	end
	if (a==3) then
	roamW(controllable,sense,pos)
	end

end

function friend(controllable,sense)
	actor=sense:getPlayer(controllable,true)
	if not (actor == nil ) then
		pos=actor:getPosition()	
		me=controllable:getPosition()	
		if (pos:getDistance(me)>2) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(pos.x,pos.y)
			end
		end
	end
end 

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
		if (controllable:getFlag("subverted")==1) then
			friend(controllable,sense)
		else
			robot(controllable,sense)
		end
	end
end  
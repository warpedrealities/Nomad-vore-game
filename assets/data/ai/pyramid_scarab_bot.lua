
function robot(controllable,sense)
	a=math.random(0,8)
	controllable:move(a);
end

function melee(controllable,sense,hostile)
	if hostile:getRPG():hasStatus(17) then
		controllable:setValue(2,1)	
		controllable:specialCommand("hide")
		if controllable:HasPath() then
			controllable:FollowPath()
		end
	else
		controllable:setAttack(1)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			

	end

end

function combat(controllable,sense,hostile,pos)
	if (controllable:getFlag("overdrive")==1) and (controllable:getValue(0)==0)then
		controllable:useSelfMove(3)
		controllable:setValue(0,1)	
	end
	if pos:getDistance(hostile:getPosition())<2 and controllable:getValue(2)==0 then
	--if in melee range attack
		 melee(controllable,sense,hostile)
	else
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	end

end

function repair(controllable,sense)
	x=controllable:getValue(1)	
	y=controllable:getValue(2)	
	if (x>0) and (y>0) then
	repairTarget= sense:getActorInTile(x,y)
		if (repairTarget==nil) then
			repairTarget=nil;
			controllable:setValue(1,0)	
			controllable:setValue(2,0)
			return false;	
		end
		
		if not (repairTarget:getName()=="Black guardian") then
			repairTarget=nil;
			controllable:setValue(1,0)	
			controllable:setValue(2,0)
			return false;
		end

	end
	
	if not (repairTarget==nil) then
		if (repairTarget:getRPG():getStat(0)>50) then

			repairTarget=nil;
			controllable:setValue(1,0)	
			controllable:setValue(2,0)
			return false;
		end
		controllable:setAttack(2)
		controllable:Attack(repairTarget:getPosition().x,repairTarget:getPosition().y)	
		controllable:setValue(1,repairTarget:getPosition().x)	
		controllable:setValue(2,repairTarget:getPosition().y)			
	
		return true;
	end
	
	repairTarget=sense:getActor(controllable,10,false,sense:getCriteria("healthBelow,0.25,name,Black guardian","healthAbove",0));
	if not (repairTarget==nil) then
		if pos:getDistance(repairTarget:getPosition())<2 then	
		controllable:setAttack(2)
		controllable:Attack(repairTarget:getPosition().x,repairTarget:getPosition().y)	
		print("repair 2",repairTarget:getRPG():getStat(0))		
		controllable:setValue(1,repairTarget:getPosition().x)	
		controllable:setValue(2,repairTarget:getPosition().y)			
		else
			if controllable:HasPath() then
			controllable:FollowPath()
			else
			controllable:Pathto(repairTarget:getPosition().x,repairTarget:getPosition().y,2)
			end	
		end
		return true;
	end
	return false;
	
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	if controllable:HasPath() then
		controllable:FollowPath()
	else 
		if not repair(controllable,sense) then
			hostile=sense:getHostile(controllable,10,true)
			if not (hostile == nil ) and not controllable:isPeace() then
			--combat ai here
			combat(controllable,sense,hostile,pos)
			else
			robot(controllable,sense)
			end
		end
	end
end  
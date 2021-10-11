package com.mood.userservice.service;

import com.mood.userservice.dto.BlockUserDto;
import com.mood.userservice.jpa.BlockUserEntity;
import com.mood.userservice.jpa.BlockUserRepository;
import com.mood.userservice.jpa.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BlockUserServiceImpl implements BlockUserService{
    BlockUserRepository blockUserRepository;

    public BlockUserServiceImpl(BlockUserRepository blockUserRepository){
        this.blockUserRepository=blockUserRepository;
    }

    @Override
    public boolean updateBlockUsers(String userUid, List<BlockUserDto> blockUserDtoList) {
        boolean check = true;
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Optional<Iterable<BlockUserEntity>> optionalBlockUserEntities = blockUserRepository.findByUserUid(userUid);
        if(optionalBlockUserEntities.isPresent()){
            Iterable<BlockUserEntity> iterable = optionalBlockUserEntities.get();
            List<BlockUserEntity> list = new ArrayList<>();
            iterable.forEach(v->
                    list.add(new ModelMapper().map(v, BlockUserEntity.class)));
            for (BlockUserDto blockUserDto : blockUserDtoList) {
                check=true;
                blockUserDto.setBlockTime(LocalDateTime.now());
                blockUserDto.setBlockUid(UUID.randomUUID().toString());
                for (BlockUserEntity blockUserEntity : list){
                    if(blockUserDto.getPhoneNum().equals(blockUserEntity.getPhoneNum())){
                            updateBlockUser(blockUserDto);
                            check=false;
                            break;
                    }
                }
                if(check){
                    BlockUserEntity blockUserEntity = mapper.map(blockUserDto, BlockUserEntity.class);
                    blockUserRepository.save(blockUserEntity);
                }
            }
        }else {
            for (BlockUserDto blockUserDto : blockUserDtoList) {
                blockUserDto.setBlockTime(LocalDateTime.now());
                blockUserDto.setBlockUid(UUID.randomUUID().toString());
                BlockUserEntity blockUserEntity = mapper.map(blockUserDto, BlockUserEntity.class);
                blockUserRepository.save(blockUserEntity);
            }
        }
        return false;
    }

    public void updateBlockUser(BlockUserDto blockUserDto){
        Optional<BlockUserEntity> optional = blockUserRepository.findByUserUidAndPhoneNum(blockUserDto.getUserUid(), blockUserDto.getPhoneNum());
        optional.ifPresent(selectUser->{
            selectUser.setBlockTime(LocalDateTime.now());
            selectUser.setDisabled(!blockUserDto.isDisabled());
            blockUserRepository.save(selectUser);
        });
    }

    @Override
    public List<String> getBlockUsers(String userUid) {
        List<String> list = new ArrayList<>();
        Optional<Iterable<BlockUserEntity>> optional = blockUserRepository.findByUserUidAndDisabled(userUid, false);
        if(optional.isPresent()){
            Iterable<BlockUserEntity> iterable = optional.get();
            iterable.forEach(v->
                    list.add(v.getPhoneNum()));
            return list;
        }
        return list;
    }
}

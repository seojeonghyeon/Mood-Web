package com.mood.userservice.service;

import com.mood.userservice.dto.BlockUserDto;

import java.util.List;

public interface BlockUserService {
    boolean updateBlockUsers(String userUid, List<BlockUserDto> blockUserDtoList);
    List<BlockUserDto> getBlockUsers(String userUid);
}

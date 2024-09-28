package aphamale.project.appointment.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import aphamale.project.appointment.Domain.HospitalInfoDomain;
import aphamale.project.appointment.Domain.HospitalReserveDomain;
import aphamale.project.appointment.Dto.HospitalInfoDto;
import aphamale.project.appointment.Dto.HospitalReserveDto;
import aphamale.project.appointment.Repository.HospitalInfoRepository;
import aphamale.project.appointment.Service.HospitalReserveService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@ResponseBody
public class AdminController {

    private final HospitalInfoRepository hospitalInfoRepository;
    private final HospitalReserveService hospitalReserveService;

    public AdminController(HospitalInfoRepository hospitalInfoRepository, HospitalReserveService hospitalReserveService){
        this.hospitalInfoRepository = hospitalInfoRepository;
        this.hospitalReserveService = hospitalReserveService;
    }

    @PostMapping("/api/admin")
    public String getAdmin(@RequestBody HospitalInfoDto hospitalInfoDto) {
    //public HospitalInfoDomain getAdmin(@RequestBody HospitalInfoDto hospitalInfoDto) {
    //public Object[] getAdmin(@RequestBody HospitalInfoDto hospitalInfoDto) {
        // 병원ID
        String hospitalId = hospitalInfoDto.getHospitalId();

        // 해당 계정의 정보 조회
        HospitalInfoDomain hospitalInfoDomain = hospitalInfoRepository.findByHospitalId(hospitalId).orElseThrow(() -> new UsernameNotFoundException(hospitalId));

        String hospitalName = hospitalInfoDomain.getHospitalName();
        //String refreshToken = hospitalInfoDomain.getJwtRefresh();

        return hospitalName;
        //return hospitalInfoDomain;
        //return new Object[] {hospitalName, refreshToken};
    }
    
    // 예약 내역 관리 조회 
    @PostMapping("/api/admin/reserveList")
    public List<HospitalReserveDto> getReserveList(@RequestBody Map<String, String> searchParam) {

        // 프론트에 보낼 LIST
        List<HospitalReserveDto> finalHospitalList = new ArrayList<HospitalReserveDto>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try{

            String hospitalId = searchParam.get("hospitalId");
            Date fromDate = format.parse(searchParam.get("fromDate"));
            Date toDate = format.parse(searchParam.get("toDate"));
                      
            // groupId 찾기
            HospitalInfoDomain hospitalInfoDomain = hospitalInfoRepository.findByHospitalId(hospitalId).orElseThrow(() -> new UsernameNotFoundException(hospitalId));
            String groupId = hospitalInfoDomain.getGroupId();
    
            List<HospitalReserveDomain> reserveList = hospitalReserveService.selectReserveList(hospitalId, groupId, fromDate, toDate);
    
            if(reserveList != null){

                finalHospitalList = reserveList.stream()
                                    .map(m -> new HospitalReserveDto(m.getReservePk(), m.getHospitalName(), m.getHospitalAddres(),
                                        m.getSubject(), m.getReserveDate(), m.getReserveTime(), m.getAlarmFlag(), m.getReserveStatus(),
                                        m.getRemark(), m.getInsertUser(), m.getInsertDate(), m.getUpdateUser(), m.getUpdateDate()))
                                    .collect(Collectors.toList());
            }     
        
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
        return finalHospitalList; 
    }
}

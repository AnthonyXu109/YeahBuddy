package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Result;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.ResultRepository;
import cn.edu.xmu.yeahbuddy.model.ResultDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ResultService {

    @NonNls
    private static Log log = LogFactory.getLog(ResultService.class);

    private final ResultRepository resultRepository;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param resultRepository Autowired
     */
    @Autowired
    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    /**
     * 查找综合评审报告
     *
     * @param id 评审报告报告主键
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Result> findById(int id) {
        log.debug("Finding Result with key " + id);
        return resultRepository.findById(id);
    }

    /**
     * 查找评审报告
     *
     * @param report 报告
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Result> findByReport(Report report) {
        log.debug("Finding Result");
        return resultRepository.findByReport(report);
    }

    /**
     * 查找未评审完的评审报告
     *
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public List<Result> findBySubmittedFalse(){ return resultRepository.findBySubmittedFalse(); }

    /**
     * 查找评审完的评审报告
     *
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public List<Result> findBySubmittedTrue(){ return resultRepository.findBySubmittedTrue(); }


    /**
     * 按团队查找评审报告
     *
     * @param team 团队
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public List<Result> findByTeam(Team team) {
        log.debug("Finding Result");
        return resultRepository.findByTeam(team);
    }

    /**
     * 新建综合评审报告
     *
     * @param report 目标报告
     * @return 新注册的综合评审报告
     */
    @Transactional
    public Result createResult(Report report) throws IdentifierAlreadyExistsException {
        log.debug(String.format("Trying to create Result: %s", report));
        if (resultRepository.findByReport(report).isPresent()) {
            log.info(String.format("Fail to create Result with id: %s: report already exist", report));
            throw new IdentifierAlreadyExistsException("review.id.exist", null);
        }

        Result result = new Result(report, "未评审");
        result = resultRepository.save(result);
        log.debug(String.format("Created new Result with report: %s", report));
        return result;
    }

    /**
     * 删除团队综合评审报告
     *
     * @param id 综合评审报告主键
     */
    @Transactional
    public void deleteResult(int id) {
        log.debug("Delete Report with id" + id);
        resultRepository.deleteById(id);
    }

    @Transactional
    public Result updateResult(int id, ResultDto dto) {
        log.debug("Trying to update Result with id" + id);
        Optional<Result> r = resultRepository.queryById(id);

        if (!r.isPresent()) {
            log.info("Failed to load Result " + id + ": not found");
            throw new IdentifierNotExistsException("result.id.not_found", id);
        }
        Result result = r.get();
        if (dto.getSubmitted() != null) {
            log.trace("Updated submitted for Result with id " + id + ":" + result.isSubmitted() +
                              " -> " + dto.getSubmitted());
            result.setSubmitted(dto.getSubmitted());
        }

        if (dto.getContent() != null) {
            log.trace("Updated content for Result with id " + id);
            result.setContent(dto.getContent());
        }

        if (dto.getBrief() != null) {
            log.trace("Updated rank for Result with id " + id + ":" + result.getBrief() +
                              " -> " + dto.getBrief());
            result.setBrief(dto.getBrief());
        }

        return resultRepository.save(result);
    }
}

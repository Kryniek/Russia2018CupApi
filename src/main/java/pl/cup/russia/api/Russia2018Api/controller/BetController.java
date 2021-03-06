package pl.cup.russia.api.Russia2018Api.controller;

import static pl.cup.russia.api.Russia2018Api.enums.BetType.GROUP_STAGE_PROMOTION;
import static pl.cup.russia.api.Russia2018Api.enums.BetType.MATCH_RESULT;
import static pl.cup.russia.api.Russia2018Api.enums.BetType.WORLD_CUP_WINNER;
import static pl.cup.russia.api.Russia2018Api.util.BetValidator.canBet;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.cup.russia.api.Russia2018Api.definition.BetService;
import pl.cup.russia.api.Russia2018Api.definition.LeagueService;
import pl.cup.russia.api.Russia2018Api.definition.MatchService;
import pl.cup.russia.api.Russia2018Api.dto.BetValue;
import pl.cup.russia.api.Russia2018Api.enums.DBOperation;
import pl.cup.russia.api.Russia2018Api.enums.StaticHtmlResource;

@Controller
@RequestMapping("/bets")
public class BetController {

	@Autowired
	private BetService service;

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private MatchService matchService;

	@GetMapping("/sync")
	@ResponseBody
	public void syncBets() {
		service.calculatePointsAndUpdateBetRecords();
	}

	@PostMapping("/winner/{winnerTeamName}")
	public String betWorldCupWinner(@PathVariable String winnerTeamName, RedirectAttributes redirectAttributes) {
		service.createWorldCupWinnerBet(winnerTeamName);

		return getWorldCupWinnerRedirectWithAttributes(redirectAttributes, DBOperation.INSERTED);
	}

	@PostMapping("/winner/update/{winnerTeamName}")
	public String updateBetWorldCupWinner(@PathVariable String winnerTeamName, RedirectAttributes redirectAttributes) {
		service.updateBetByType(WORLD_CUP_WINNER, new BetValue(winnerTeamName));

		return getWorldCupWinnerRedirectWithAttributes(redirectAttributes, DBOperation.UPDATED);
	}

	@PostMapping("/groups")
	@ResponseBody
	public void betGroupPromotion(@RequestBody List<BetValue> betValues) {
		service.createGroupPromotionBets(betValues);
	}

	@PutMapping("/groups")
	@ResponseBody
	public Integer updateBetGroupPromotion(@RequestBody List<BetValue> betValues) {
		return service.updateBetsByType(GROUP_STAGE_PROMOTION, betValues);
	}

	@PostMapping("/match/{matchId}/{hometeamScore}/{awayteamScore}")
	public String betMatchScore(@PathVariable Integer matchId, @PathVariable Integer hometeamScore,
			@PathVariable Integer awayteamScore, RedirectAttributes redirectAttributes) {
		BetValue betValue = new BetValue();
		betValue.setMatchId(matchId);
		betValue.setHometeamScore(hometeamScore);
		betValue.setAwayteamScore(awayteamScore);

		service.createMatchScoreBet(betValue);

		return getGroupsWinnersRedirectWithAttributes(redirectAttributes, DBOperation.INSERTED, matchId);
	}

	@PostMapping("/match/update/{matchId}/{hometeamScore}/{awayteamScore}")
	public String updateBetMatchScore(@PathVariable Integer matchId, @PathVariable Integer hometeamScore,
			@PathVariable Integer awayteamScore, RedirectAttributes redirectAttributes) {
		BetValue betValue = new BetValue();
		betValue.setMatchId(matchId);
		betValue.setHometeamScore(hometeamScore);
		betValue.setAwayteamScore(awayteamScore);

		service.updateBetByType(MATCH_RESULT, betValue);

		return getGroupsWinnersRedirectWithAttributes(redirectAttributes, DBOperation.UPDATED, matchId);
	}
	
	private String getWorldCupWinnerRedirectWithAttributes(RedirectAttributes redirectAttributes,
			DBOperation operation) {
		redirectAttributes.addFlashAttribute("teams", leagueService.selectAllTeams());
		redirectAttributes.addFlashAttribute("userBet", service.selectUserBetByType(WORLD_CUP_WINNER));
		redirectAttributes.addFlashAttribute("canYouBet", canBet());
		redirectAttributes.addFlashAttribute("operation", operation.name());

		return StaticHtmlResource.WORLD_CUP_WINNER.getKebabCasedRedirectValue();
	}

	private String getGroupsWinnersRedirectWithAttributes(RedirectAttributes redirectAttributes, DBOperation operation,
			Integer matchId) {
		redirectAttributes.addFlashAttribute("match", matchService.selectByMatchApiId(matchId));
		redirectAttributes.addFlashAttribute("userBet", service.selectUserMatchBet(matchId));
		redirectAttributes.addFlashAttribute("canYouBet", canBet());
		redirectAttributes.addFlashAttribute("operation", operation.name());

		return StaticHtmlResource.BET.getKebabCasedRedirectValue() + "/" + matchId.intValue();
	}
}
